package net.cryptic_game.backend.base.api.handler.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.executor.ApiExecutor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.network.server.http.route.HttpRoute;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public final class RestApiLocation implements HttpRoute {

    private static final String EMPTY_RESPONSE = "{}";
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public Publisher<Void> execute(final HttpServerRequest request, final HttpServerResponse response, final ApiAuthenticationProvider authenticationProvider) {
        return response.sendString(request.receive()
                .aggregate()
                .asString()
                .defaultIfEmpty("{}")
                .flatMap(content -> {
                    final ApiRequest apiRequest = new ApiRequest(
                            request.path().substring(request.path().indexOf('/') + 1),
                            content.isBlank() ? JsonUtils.EMPTY_OBJECT : JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class),
                            authenticationProvider.resolveGroups(request.requestHeaders().get(HttpHeaderNames.AUTHORIZATION)),
                            null
                    );
                    return ApiExecutor.execute(this.endpoints, apiRequest, authenticationProvider);
                })
                .onErrorReturn(cause -> cause instanceof JsonSyntaxException || cause.getCause() instanceof JsonSyntaxException,
                        new ApiResponse(ApiResponseStatus.BAD_REQUEST, "JSON_SYNTAX"))
                .flatMap(apiResponse -> {
                    final ApiResponseStatus responseStatus = apiResponse.getResponseCode();

                    response.status(HttpResponseStatus.valueOf(responseStatus.getCode(), responseStatus.getName()));
                    response.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);

                    final boolean hasData = apiResponse.getJson() != null;
                    final boolean hasMessage = apiResponse.getError() != null;

                    if (!(hasData || hasMessage)) {
                        if (responseStatus.isError()) return Mono.just(JsonBuilder.create("error", responseStatus.toString()).toString());
                        return Mono.just(EMPTY_RESPONSE);
                    }

                    final JsonElement jsonResponse = hasData
                            ? apiResponse.getJson()
                            : JsonBuilder.create("error", apiResponse.getError()).build();

                    return Mono.just(jsonResponse.toString());
                })
        );
    }
}
