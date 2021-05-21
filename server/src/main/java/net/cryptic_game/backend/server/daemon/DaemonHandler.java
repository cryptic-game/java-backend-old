package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.data.sql.repositories.server_management.DisabledEndpointRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLHandshakeException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public final class DaemonHandler {

    private static final String CHARSET = StandardCharsets.UTF_8.toString();
    private static final AsciiString CONTENT_TYPE = AsciiString.of(HttpHeaderValues.APPLICATION_JSON + "; " + HttpHeaderValues.CHARSET + "=" + CHARSET);

    private final String apiToken;
    private final ApplicationContext context;
    private final DisabledEndpointRepository disabledEndpointRepository;
    private final ApiAuthenticator authenticator;

    @Setter
    private Map<String, ApiEndpointData> endpoints;
    private Object daemonSendObject;
    private Method daemonSendMethod;
    private List<ApiParameterData> daemonSendMethodParameters;

    public void registerDaemon(final String name, final String url) {
        final Daemon daemon = new Daemon(
                name,
                HttpClient.create()
                        .baseUrl(url)
                        .headers(headers -> {
                            headers.set(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE);
                            headers.set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON);
                            headers.set(HttpHeaderNames.ACCEPT_CHARSET, CHARSET.toLowerCase(Locale.ENGLISH));
                            headers.set(HttpHeaderNames.USER_AGENT, "Cryptic-Server");
                            if (!this.apiToken.isBlank()) headers.set(HttpHeaderNames.AUTHORIZATION, this.apiToken);
                        })
                        .compress(true)
                        .followRedirect(true)
                        .responseTimeout(Duration.ofSeconds(10))
        );

        daemon.getHttpClient()
                .get()
                .uri("/daemon/endpoints")
                .responseSingle((response, byteBufMono) -> {
                    if (response.status().equals(HttpResponseStatus.UNAUTHORIZED))
                        return Mono.error(new DaemonException("Invalid api token"));
                    if (!response.status().equals(HttpResponseStatus.OK))
                        return Mono.error(new DaemonException(String.format("Unexpected staus from daemon: %s", response.status())));
                    return byteBufMono.asString();
                })
                .retry(2)
                .map(content -> JsonUtils.fromJson(JsonParser.parseString(content), JsonArray.class))
                .onErrorMap(cause -> cause instanceof JsonParseException,
                        cause -> new DaemonException("unexpected json from daemon", cause))
                .subscribe(
                        endpoints -> {
                            // set disabled endpoints from database disabled in the json
                            for (JsonElement endpointsObject : endpoints) {
                                JsonArray endpointList = ((JsonObject) endpointsObject).getAsJsonArray("endpoints");
                                for (JsonElement endpoint : endpointList) {
                                    if (this.disabledEndpointRepository.existsById(
                                            ((JsonObject) endpointsObject).get("id").getAsString()
                                                    + "/"
                                                    + ((JsonObject) endpoint).getAsJsonPrimitive("id").getAsString())) {
                                        ((JsonObject) endpoint).addProperty("disabled", true);
                                    }
                                }
                            }
                            this.addEndpointCollections(DaemonUtils.parseDaemonEndpoints(daemon, endpoints, ApiType.WEBSOCKET));
                            log.info("Successfully registered daemon {}.", daemon.getName());
                        },
                        cause -> {
                            if (cause instanceof DaemonException) {
                                log.error("Unable to regierter daemon {}: {}", daemon.getName(), cause.getMessage());
                            } else if (cause.getMessage().contains("refused") || cause.getMessage().contains("cert")) {
                                log.error("Unable to connect to daemon {}: {}", daemon.getName(), cause.getMessage());
                            } else if (cause instanceof SSLHandshakeException) {
                                final StringBuilder message = new StringBuilder();
                                Throwable currentCause = cause;

                                while (currentCause != null) {
                                    message.append(currentCause.toString()).append(" ");
                                    currentCause = currentCause.getCause();
                                }

                                log.error("Unable to perform tls handshake on daemon {}: {}", daemon.getName(), message.toString().strip());
                            } else {
                                log.error("Unable to register daemon {}.", daemon.getName(), cause);
                            }
                            SpringApplication.exit(this.context);
                        }
                );
    }

    public void addEndpointCollections(final Set<ApiEndpointCollectionData> endpointCollections) {
        this.endpoints.putAll(ApiEndpointCollectionParser.getEndpoints(
                endpointCollections.parallelStream()
                        .peek(collection -> collection.getEndpoints().forEach((name, endpoint) -> {
                            endpoint.setMethod(this.daemonSendMethod);
                            endpoint.setInstance(this.daemonSendObject);
                            endpoint.setAuthenticator(this.authenticator);
                            final List<ApiParameterData> parameters = new ArrayList<>(this.daemonSendMethodParameters);
                            Collections.addAll(parameters, endpoint.getParameters());
                            endpoint.setParameters(parameters.toArray(ApiParameterData[]::new));
                        }))
                        .collect(Collectors.toSet())
        ).entrySet().parallelStream()
                .map(entry -> {
                    entry.getValue().setId(entry.getKey());
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public void setSend(final Object sendObject, final Method sendMethod) {
        this.daemonSendObject = sendObject;
        this.daemonSendMethod = sendMethod;
        this.daemonSendMethodParameters = parseParameters(this.daemonSendMethod.getParameters());
    }

    public List<ApiParameterData> parseParameters(final Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(this::parseParameter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ApiParameterData parseParameter(final Parameter parameter) {
        if (parameter.isAnnotationPresent(ApiParameter.class)) {
            final ApiParameter apiParameter = parameter.getAnnotation(ApiParameter.class);
            return new ApiParameterData(apiParameter.id(), apiParameter.required(), String.join("\n", apiParameter.description()), parameter.getType(), apiParameter.type());
        } else return null;
    }
}
