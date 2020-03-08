package net.cryptic_game.backend.daemon.client;

import java.util.HashSet;
import java.util.Set;
import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class NettyClientHandler {

    private final Set<NettyClient> clients;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public void addClient(final String name, final String host, final int port, final NettyCodec nettyCodec) {
        this.clients.add(new NettyClient(name, host, port, this.eventLoopGroupHandler, nettyCodec));
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }
}
