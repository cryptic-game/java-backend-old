package net.cryptic_game.backend.base.netty.client;

import java.util.HashSet;
import java.util.Set;

public final class NettyClientHandler {

    private final Set<NettyClient> clients;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
    }

    public NettyClient addClient(final NettyClient client) {
        this.clients.add(client);
        return client;
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }

    public void stop() {
        this.clients.forEach(NettyClient::close);
    }
}
