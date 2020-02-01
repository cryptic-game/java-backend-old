package net.cryptic_game.backend.server.server.http;

import net.cryptic_game.backend.server.server.ServerCodec;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;
import net.cryptic_game.backend.server.server.http.endpoints.PlayerLeaderboardEndpoint;
import net.cryptic_game.backend.server.server.http.endpoints.PlayersOnlineEndpoint;

import java.util.HashMap;
import java.util.Map;

public class HttpCodec implements ServerCodec {

    private final Map<String, HttpEndpoint> endpoints;
    private final ServerCodecInitializer initializer;

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
    public ServerCodecInitializer getInitializer() {
        return this.initializer;
    }
}
