package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.AsciiString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import reactor.core.publisher.Mono;
import reactor.netty.http.HttpProtocol;
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

    private final Map<String, ApiEndpointData> endpoints;
    private final String apiToken;
    private final Bootstrap bootstrap;
    private final ApiAuthenticator authenticator;

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
                        .protocol(HttpProtocol.HTTP11, HttpProtocol.H2)
                        .secure(spec -> spec.sslContext(SslContextBuilder.forClient()))
                        .responseTimeout(Duration.ofSeconds(10))
        );

        daemon.getHttpClient()
                .get()
                .uri("/daemon/endpoints")
                .responseSingle((response, byteBufMono) -> response.status().equals(HttpResponseStatus.UNAUTHORIZED)
                        ? Mono.error(new DaemonException("Invalid api token"))
                        : byteBufMono.asString())
                .map(content -> JsonUtils.fromJson(JsonParser.parseString(content), JsonArray.class))
                .onErrorMap(cause -> cause instanceof JsonParseException || cause instanceof JsonTypeMappingException,
                        cause -> new DaemonException("unexpected json from daemon", cause))
                .retry(2)
                .subscribe(
                        endpoints -> {
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
                            this.bootstrap.shutdown();
                        }
                );
    }

    public void addEndpointCollections(final Set<ApiEndpointCollectionData> endpointCollections) {
        this.endpoints.putAll(ApiEndpointCollectionParser.getEndpoints(
                endpointCollections.parallelStream()
                        .peek(collection -> collection.getEndpoints().forEach((name, endpoint) -> {
                            endpoint.setMethod(this.daemonSendMethod);
                            endpoint.setInstance(this.daemonSendObject);
                            endpoint.setAuthenticator(authenticator);
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
            return new ApiParameterData(apiParameter.id(), apiParameter.required(), apiParameter.type(), String.join("\n", apiParameter.description()), parameter.getType());
        } else return null;
    }
}
