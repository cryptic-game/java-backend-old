package net.cryptic_game.backend.base.netty.server;

import java.util.HashSet;
import java.util.Set;

public final class NettyServerHandler {

    private final Set<NettyServer> servers;

    public NettyServerHandler() {
        this.servers = new HashSet<>();
    }

    public void addServer(final NettyServer server) {
        this.servers.add(server);
    }

    public void start() {
        this.servers.forEach(NettyServer::start);
    }

    public void stop() {
        this.servers.forEach(NettyServer::close);
    }
}
