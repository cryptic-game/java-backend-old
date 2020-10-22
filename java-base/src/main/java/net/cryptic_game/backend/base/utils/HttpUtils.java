package net.cryptic_game.backend.base.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HttpUtils {

    private HttpUtils() {
        throw new UnsupportedOperationException();
    }

    public static Publisher<Void> sendStatus(final HttpServerResponse response, final HttpResponseStatus status) {
        return sendStatus(response, status, true);
    }

    public static Publisher<Void> sendStatus(final HttpServerResponse response, final HttpResponseStatus status, final boolean body) {
        response.status(status);
        return body
                ? response.header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN).sendString(Mono.just(status.toString()))
                : response.send();
    }

    public static Publisher<Void> sendStatus(final HttpServerResponse response, final HttpResponseStatus status, final String message) {
        return response.status(status)
                .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .sendString(Mono.just(status.toString() + ": " + message));
    }

    public static Optional<Publisher<Void>> checkMethod(final HttpServerRequest request, final HttpServerResponse response, final HttpMethod... methods) {
        for (final HttpMethod method : methods) {
            if (request.method().equals(method)) {
                return Optional.empty();
            }
        }

        final String allowValue = Arrays.stream(methods)
                .map(HttpMethod::asciiName)
                .collect(Collectors.joining(", "));
        response.header(HttpHeaderNames.ALLOW, allowValue);

        return Optional.of(HttpUtils.sendStatus(response, HttpResponseStatus.METHOD_NOT_ALLOWED));
    }
}
