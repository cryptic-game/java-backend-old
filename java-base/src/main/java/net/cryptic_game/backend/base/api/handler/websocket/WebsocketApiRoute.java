package net.cryptic_game.backend.base.api.handler.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.executor.ApiExecutor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.network.server.http.route.WebsocketRoute;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public final class WebsocketApiRoute implements WebsocketRoute {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Map<String, ApiEndpointData> endpoints;
    private final Set<WebsocketApiContext> contexts;

    @Override
    public Publisher<Void> apply(final WebsocketInbound inbound, final WebsocketOutbound outbound) {
        final WebsocketApiContext context = new WebsocketApiContext(inbound, outbound);
        this.contexts.add(context);

        return outbound.sendString(
                inbound.receive()
                        .asString(CHARSET)
                        .filter(content -> !(content.isEmpty() && content.isBlank()))
                        .flatMap(content -> this.execute(context, content))
                        .onErrorResume(this::handleError)
                        .map(this::parseResponse)
                        .doFinally(signal -> this.contexts.remove(context)),
                CHARSET
        );
    }

    private Mono<ApiResponse> execute(final WebsocketApiContext context, final String content) {
        final JsonObject json;
        try {
            json = content.isEmpty() && content.isBlank() ? JsonUtils.EMPTY_OBJECT : JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class);
        } catch (JsonParseException e) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "JSON_SYNTAX"));
        }

        final String tag = JsonUtils.fromJson(json.get("tag"), String.class);
        final JsonElement endpoint = json.get("endpoint");

        final Mono<ApiResponse> response;

        if (tag == null) {
            response = Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MISSING_TAG"));
        } else if (endpoint == null || endpoint.isJsonNull()) {
            response = Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MISSING_ENDPOINT"));
        } else {
            response = ApiExecutor.execute(
                    this.endpoints,
                    new WebsocketApiRequest(
                            tag,
                            JsonUtils.fromJson(endpoint, String.class),
                            Optional.ofNullable(JsonUtils.fromJson(json.get("data"), JsonObject.class)).orElse(JsonUtils.EMPTY_OBJECT),
                            context
                    )
            );
        }

        return response.doOnNext(resp -> {
            if (tag != null) resp.setTag(tag);
        });
    }

    private String parseResponse(final ApiResponse response) {
        final JsonObject status = JsonBuilder.create("code", response.getStatus().code())
                .add("name", response.getStatus().reasonPhrase())
                .build();

        final JsonBuilder builder = JsonBuilder.create("status", status);

        if (response.getTag() != null) builder.add("tag", response.getTag());
        if (response.getError() != null) builder.add("error", response.getError());
        if (response.getJson() != null) builder.add("data", response.getJson());

        return builder.build().toString();
    }

    private Mono<ApiResponse> handleError(final Throwable cause) {
        log.error("Error while executing websocket api pipeline.", cause);
        return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
