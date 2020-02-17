package net.cryptic_game.backend.server.server;

import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.util.HashSet;
import java.util.Set;

public class NettyServerHandler {

    private final Set<NettyServer> servers;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyServerHandler() {
        this.servers = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public void addServer(final String name, final String host, final int port, final NettyCodec nettyCodec) {
        this.servers.add(new NettyServer(name, host, port, this.eventLoopGroupHandler, nettyCodec));
    }

    public void start() {
        this.servers.forEach(NettyServer::start);
    }
}
