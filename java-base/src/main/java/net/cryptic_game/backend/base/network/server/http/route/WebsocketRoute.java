package net.cryptic_game.backend.base.network.server.http.route;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.utils.HttpUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.util.function.BiFunction;

public interface WebsocketRoute extends HttpRoute, BiFunction<WebsocketInbound, WebsocketOutbound, Publisher<Void>> {

    @Override
    default Publisher<Void> execute(final HttpServerRequest request, final HttpServerResponse response) {
        return HttpUtils.checkMethod(request, response, HttpMethod.GET)
                .orElseGet(() ->
                        request.requestHeaders().containsValue(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)
                                ? response.sendWebsocket(this)
                                : response.status(HttpResponseStatus.BAD_REQUEST)
                                .header(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                                .sendString(Mono.justOrEmpty("not a WebSocket handshake request: missing upgrade"))
                );
    }

    @Override
    Publisher<Void> apply(WebsocketInbound inbound, WebsocketOutbound outbound);
}
