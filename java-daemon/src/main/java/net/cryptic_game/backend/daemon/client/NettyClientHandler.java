package net.cryptic_game.backend.daemon.client;

import io.netty.channel.unix.DomainSocketAddress;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.util.HashSet;
import java.util.Set;

public class NettyClientHandler {

    private final Set<NettyClient> clients;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public NettyClient addClient(final String name, final DomainSocketAddress socketAddress, final NettyCodec nettyCodec) {
        final NettyClient client = new NettyClient(name, socketAddress, this.eventLoopGroupHandler, nettyCodec);
        this.clients.add(client);
        return client;
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }
}
