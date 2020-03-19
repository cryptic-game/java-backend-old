package net.cryptic_game.backend.base.netty.client;

import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class NettyClientHandler {

    private final Set<NettyClient> clients;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public NettyClient addClient(final String name, final SocketAddress address, final boolean unixSocket, final NettyCodec nettyCodec) {
        final NettyClient client = new NettyClient(name, address, unixSocket, this.eventLoopGroupHandler, nettyCodec);
        this.clients.add(client);
        return client;
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }
}
