package net.cryptic_game.backend.base.netty.client;

import io.netty.channel.Channel;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class NettyClientHandler {

    private final Set<NettyClient> clients;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public NettyClient addClient(final String name,
                                 final SocketAddress address,
                                 final boolean unixSocket,
                                 final NettyCodec<?> nettyCodec,
                                 final Consumer<Channel> connectCallback) {
        final NettyClient client = new NettyClient(name, address, unixSocket, this.eventLoopGroupHandler, nettyCodec, connectCallback);
        this.clients.add(client);
        return client;
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }
}
