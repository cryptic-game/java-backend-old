package net.cryptic_game.backend.base.api.handler.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.executor.ApiExecutor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.network.server.http.route.HttpRoute;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
final class RestApiRoute implements HttpRoute {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final AsciiString CONTENT_TYPE = AsciiString.of(HttpHeaderValues.APPLICATION_JSON + "; " + HttpHeaderValues.CHARSET + "=" + CHARSET.toString());
    private static final String EMPTY_RESPONSE = "{}";
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public Publisher<Void> execute(final HttpServerRequest httpRequest, final HttpServerResponse httpResponse) {
        return httpResponse.sendString(
                httpRequest.receive()
                        .aggregate()
                        .asString(CHARSET)
                        .flatMap(this::parse)
                        .defaultIfEmpty(JsonUtils.EMPTY_OBJECT)
                        .flatMap(json -> this.execute(httpRequest, json))
                        .onErrorResume(this::handleError)
                        .flatMap(apiResponse -> this.parseResponse(httpResponse, apiResponse)),
                CHARSET
        );
    }

    private Mono<String> parseResponse(final HttpServerResponse httpResponse, final ApiResponse apiResponse) {
        httpResponse.status(apiResponse.getStatus());
        httpResponse.header(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE);

        final boolean hasData = apiResponse.getJson() != null;
        final boolean hasMessage = apiResponse.getError() != null;

        if (!hasData && !hasMessage)
            return Mono.just(
                    apiResponse.getStatus().code() < 400
                            ? EMPTY_RESPONSE
                            : JsonBuilder.create("error", apiResponse.getStatus().toString()).build().toString()
            );

        final JsonElement jsonResponse = hasData
                ? apiResponse.getJson()
                : JsonBuilder.create("error", apiResponse.getError()).build();

        return Mono.just(jsonResponse.toString());
    }

    private Mono<JsonObject> parse(final String content) {
        return content.isBlank() ? Mono.empty() : Mono.just(JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class));
    }

    private Mono<ApiResponse> execute(final HttpServerRequest httpRequest, final JsonObject json) {
        final String endpoint = httpRequest.path().substring(httpRequest.path().indexOf('/') + 1);
        final ApiRequest apiRequest = new RestApiRequest(endpoint, json, httpRequest);

        return ApiExecutor.execute(this.endpoints, apiRequest);
    }

    private Mono<ApiResponse> handleError(final Throwable cause) {
        if (cause instanceof JsonSyntaxException || (cause.getCause() != null && cause.getCause() instanceof JsonSyntaxException)) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "JSON_SYNTAX"));
        } else {
            log.error("Error while handling rest api", cause);
            return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
