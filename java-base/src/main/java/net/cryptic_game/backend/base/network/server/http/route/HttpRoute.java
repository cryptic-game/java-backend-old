package net.cryptic_game.backend.base.network.server.http.route;

import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public interface HttpRoute {

    Publisher<Void> execute(HttpServerRequest request, HttpServerResponse response);
}
