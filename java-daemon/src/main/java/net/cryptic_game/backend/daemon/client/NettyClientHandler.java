package net.cryptic_game.backend.daemon.client;

import net.cryptic_game.backend.base.netty.EventLoopGropHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NettyClientHandler {

    private final Set<NettyClient> clients;
    private final EventLoopGropHandler eventLoopGropHandler;

    public NettyClientHandler() {
        this.clients = new HashSet<>();
        this.eventLoopGropHandler = new EventLoopGropHandler();
    }

    public void addServer(final String name, final String host, final int port, final NettyCodec nettyCodec) {
        this.clients.add(new NettyClient(name, host, port, this.eventLoopGropHandler, nettyCodec));
    }

    public void start() {
        this.clients.forEach(NettyClient::start);
    }
}
