package net.cryptic_game.backend.server.server.daemon;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.daemon.endpoints.RespondEndpoint;
import net.cryptic_game.backend.server.server.daemon.endpoints.SendEndpoint;

import java.util.HashMap;
import java.util.Map;

public class DaemonCodec implements NettyCodec {

    private final DaemonHandler daemonHandler;
    private final Map<String, DaemonEndpoint> endpoints;

    public DaemonCodec(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
        this.endpoints = new HashMap<>();

        this.addEndpoint(new RespondEndpoint());
        this.addEndpoint(new SendEndpoint());
    }

    public void addEndpoint(final DaemonEndpoint endpoint) {
        this.endpoints.put(endpoint.getName().toLowerCase(), endpoint);
    }

    @Override
    public NettyInitializer getInitializer() {
        return new DaemonInitializer(this.daemonHandler, this.endpoints);
    }
}
