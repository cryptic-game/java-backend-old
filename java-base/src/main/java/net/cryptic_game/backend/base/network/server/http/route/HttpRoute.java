package net.cryptic_game.backend.base.network.server.http.route;

import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public interface HttpRoute {

    Publisher<Void> execute(HttpServerRequest request, HttpServerResponse response, ApiAuthenticationProvider authenticationProvider);
}
