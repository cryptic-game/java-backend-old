package net.cryptic_game.backend.base.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

public final class HttpUtils {

    private HttpUtils() {
        throw new UnsupportedOperationException();
    }

    public static Publisher<Void> sendStatus(final HttpServerResponse response, final HttpResponseStatus status) {
        return response.status(status)
                .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .sendString(Mono.just(status.toString()));
    }
}
