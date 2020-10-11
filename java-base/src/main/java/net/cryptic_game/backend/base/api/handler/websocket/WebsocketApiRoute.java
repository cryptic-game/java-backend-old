package net.cryptic_game.backend.base.api.handler.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.executor.ApiExecutor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.network.server.http.route.WebsocketRoute;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public final class WebsocketApiRoute implements WebsocketRoute {

    private static final ApiResponse ERROR_JSON_SYNTAX = new ApiResponse(ApiResponseStatus.BAD_REQUEST, "JSON_SYNTAX");
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public Publisher<Void> apply(final WebsocketInbound inbound, final WebsocketOutbound outbound) {
        return outbound.sendString(inbound.receiveFrames()
                .flatMap(frame -> {
                    final JsonObject json = JsonUtils.fromJson(JsonParser.parseString(frame.content().toString(StandardCharsets.UTF_8)), JsonObject.class);
                    final ApiRequest request = new ApiRequest(
                            JsonUtils.fromJson(json.get("endpoint"), String.class),
                            JsonUtils.fromJson(json.get("data"), JsonObject.class) == null ? JsonUtils.EMPTY_OBJECT : JsonUtils.fromJson(json.get("data"), JsonObject.class),
                            JsonUtils.fromJson(json.get("tag"), String.class)
                    );

                    final Mono<ApiResponse> apiResponse;
                    if (request.getTag() == null) {
                        apiResponse = Mono.just(new ApiResponse(ApiResponseStatus.BAD_REQUEST, "MISSING_TAG"));
                    } else if (request.getEndpoint() == null) {
                        apiResponse = Mono.just(new ApiResponse(ApiResponseStatus.BAD_REQUEST, "MISSING_ENDPOINT"));
                    } else {
                        apiResponse = ApiExecutor.execute(this.endpoints, request, null);
                    }

                    return apiResponse.doOnNext(response -> response.setTag(request.getTag()));
                })
                .onErrorReturn(cause -> cause instanceof JsonSyntaxException || cause.getCause() instanceof JsonSyntaxException, ERROR_JSON_SYNTAX)
                .onErrorResume(JsonTypeMappingException.class, cause -> { // FIXME: not working
                    if (cause.getCause() instanceof JsonSyntaxException) // FIXME: not working
                        return Mono.just(ERROR_JSON_SYNTAX); // FIXME: not working

                    log.error("Error while executing websocket pipeline.", cause); // FIXME: not working
                    return Mono.just(new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR)); // FIXME: not working
                }) // FIXME: not working
                .onErrorResume(cause -> { // FIXME: not working
                    log.error("Error while executing websocket pipeline.", cause); // FIXME: not working
                    return Mono.just(new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR)); // FIXME: not working
                }) // FIXME: not working
                .map(response -> {
                    final JsonBuilder builder = JsonBuilder.create(
                            "status",
                            JsonBuilder.create("code", response.getResponseCode().getCode())
                                    .add("name", response.getResponseCode().getName())
                    );

                    if (response.getTag() != null) builder.add("tag", response.getTag());
                    if (response.getError() != null) builder.add("message", response.getError());
                    if (response.getJson() != null) builder.add("data", response.getJson());

                    return builder.build().toString();
                }));
    }
}
