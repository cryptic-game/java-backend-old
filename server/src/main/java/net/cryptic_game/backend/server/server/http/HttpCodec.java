package net.cryptic_game.backend.server.server.http;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.server.server.http.endpoints.PlayerLeaderboardEndpoint;
import net.cryptic_game.backend.server.server.http.endpoints.PlayersOnlineEndpoint;

import java.util.HashMap;
import java.util.Map;

public class HttpCodec implements NettyCodec {

    private final Map<String, HttpEndpoint> endpoints;
    private final NettyInitializer initializer;

    public HttpCodec() {
        this.endpoints = new HashMap<>();
        this.initializer = new HttpServerInitializer(this.endpoints);

        this.addEndpoint(new PlayersOnlineEndpoint());
        this.addEndpoint(new PlayerLeaderboardEndpoint());
    }

    private void addEndpoint(final HttpEndpoint endpoint) {
        this.endpoints.put(endpoint.getName().toLowerCase(), endpoint);
    }

    @Override
    public NettyInitializer getInitializer() {
        return this.initializer;
    }
}
