package net.cryptic_game.backend.base.netty.server;

import net.cryptic_game.backend.base.netty.EventLoopGroupHandler;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public final class NettyServerHandler {

    private final Set<NettyServer> servers;
    private final EventLoopGroupHandler eventLoopGroupHandler;

    public NettyServerHandler() {
        this.servers = new HashSet<>();
        this.eventLoopGroupHandler = new EventLoopGroupHandler();
    }

    public void addServer(final String name, final SocketAddress address, final boolean unixSocket, final NettyCodec<?> nettyCodec) {
        this.servers.add(new NettyServer(name, address, unixSocket, this.eventLoopGroupHandler, nettyCodec));
    }

    public void start() {
        this.servers.forEach(NettyServer::start);
    }
}
