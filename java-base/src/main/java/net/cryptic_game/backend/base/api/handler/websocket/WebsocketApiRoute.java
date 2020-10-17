package net.cryptic_game.backend.base.api.handler.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
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

@Slf4j
@RequiredArgsConstructor
public final class WebsocketApiRoute implements WebsocketRoute {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public Publisher<Void> apply(final WebsocketInbound inbound, final WebsocketOutbound outbound) {
        return outbound.sendString(
                inbound.receive()
                        .asString(CHARSET)
                        .filter(content -> !content.isBlank())
                        .map(content -> this.parseRequest(JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class), inbound))
                        .flatMap(this::execute)
                        .onErrorResume(this::handleError)
                        .map(this::parseResponse),
                CHARSET
        );
    }

    private ApiRequest parseRequest(final JsonObject json, final WebsocketInbound websocketInbound) {
        return new WebsocketApiRequest(
                JsonUtils.fromJson(json.get("endpoint"), String.class),
                JsonUtils.fromJson(json.get("data"), JsonObject.class) == null ? JsonUtils.EMPTY_OBJECT : JsonUtils.fromJson(json.get("data"), JsonObject.class),
                JsonUtils.fromJson(json.get("tag"), String.class),
                websocketInbound
        );
    }

    private Mono<ApiResponse> execute(final ApiRequest request) {
        final Mono<ApiResponse> apiResponse;

        if (request.getTag() == null) {
            apiResponse = Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MISSING_TAG"));
        } else if (request.getEndpoint() == null) {
            apiResponse = Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MISSING_ENDPOINT"));
        } else {
            apiResponse = ApiExecutor.execute(this.endpoints, request);
        }

        return apiResponse.doOnNext(response -> response.setTag(request.getTag()));
    }

    private String parseResponse(final ApiResponse response) {
        final JsonObject status = JsonBuilder.create("code", response.getStatus().code())
                .add("name", response.getStatus().reasonPhrase())
                .build();

        final JsonBuilder builder = JsonBuilder.create("status", status);

        if (response.getTag() != null) builder.add("tag", response.getTag());
        if (response.getError() != null) builder.add("message", response.getError());
        if (response.getJson() != null) builder.add("data", response.getJson());

        return builder.build().toString();
    }

    private Mono<ApiResponse> handleError(final Throwable cause) {
        if (cause instanceof JsonSyntaxException) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "JSON_SYNTAX"));
        }

        log.error("Error while executing websocket pipeline.", cause);
        return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
