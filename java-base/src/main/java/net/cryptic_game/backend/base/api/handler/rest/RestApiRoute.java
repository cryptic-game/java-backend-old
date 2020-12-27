package net.cryptic_game.backend.base.api.handler.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
final class RestApiRoute implements HttpRoute {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final AsciiString CONTENT_TYPE = AsciiString.of(HttpHeaderValues.APPLICATION_JSON + "; " + HttpHeaderValues.CHARSET + "=" + CHARSET.toString());
    private static final String EMPTY_REQUEST = "";
    private static final String EMPTY_RESPONSE = "{}";
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public Publisher<Void> execute(final HttpServerRequest httpRequest, final HttpServerResponse httpResponse) {
        if (!(httpRequest.method().equals(HttpMethod.GET) || httpRequest.method().equals(HttpMethod.POST))) {
            httpResponse.header(HttpHeaderNames.ALLOW, Stream.of(HttpMethod.OPTIONS, HttpMethod.GET, HttpMethod.POST)
                    .map(HttpMethod::name)
                    .collect(Collectors.joining(", ")));
            if (httpRequest.method().equals(HttpMethod.OPTIONS)) {
                httpResponse.header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                httpResponse.header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, httpResponse.responseHeaders().get(HttpHeaderNames.ALLOW));
                httpResponse.header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaderNames.CONTENT_TYPE.toString());
                httpResponse.header(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, String.valueOf(86400));
                httpResponse.status(HttpResponseStatus.NO_CONTENT);
            } else {
                httpResponse.status(HttpResponseStatus.METHOD_NOT_ALLOWED);
            }
            return httpResponse.send();
        } else {
            httpResponse.header(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        }

        return httpResponse.sendString(
                httpRequest.receive()
                        .aggregate()
                        .asString(CHARSET)
                        .defaultIfEmpty(EMPTY_REQUEST)
                        .flatMap(content -> this.execute(httpRequest, content))
                        .onErrorResume(this::handleError)
                        .map(apiResponse -> this.parseResponse(httpResponse, apiResponse)),
                CHARSET
        );
    }

    private Mono<ApiResponse> execute(final HttpServerRequest httpRequest, final String content) {
        final JsonObject json;
        try {
            json = content.isEmpty() && content.isBlank() ? JsonUtils.EMPTY_OBJECT : JsonUtils.fromJson(JsonParser.parseString(content), JsonObject.class);
        } catch (JsonParseException e) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "JSON_SYNTAX"));
        }

        return ApiExecutor.execute(
                this.endpoints,
                new RestApiRequest(httpRequest.path(), json, new RestApiContext(httpRequest))
        );
    }

    private String parseResponse(final HttpServerResponse httpResponse, final ApiResponse apiResponse) {
        httpResponse.status(apiResponse.getStatus());
        httpResponse.header(HttpHeaderNames.CONTENT_TYPE, CONTENT_TYPE);

        final boolean hasData = apiResponse.getJson() != null;
        final boolean hasMessage = apiResponse.getError() != null;

        if (!hasData && !hasMessage) return EMPTY_RESPONSE;

        final JsonElement jsonResponse = hasData
                ? apiResponse.getJson()
                : JsonBuilder.create("error", apiResponse.getError()).build();

        return jsonResponse.toString();
    }

    private Mono<ApiResponse> handleError(final Throwable cause) {
        log.error("Error while executing rest api pipeline.", cause);
        return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
