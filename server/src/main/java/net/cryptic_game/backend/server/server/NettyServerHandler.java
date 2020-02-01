package net.cryptic_game.backend.server.server;

import java.util.ArrayList;
import java.util.List;

public class NettyServerHandler {

    private final List<NettyServer> servers;
    private final EventLoopGropHandler eventLoopGropHandler;

    public NettyServerHandler() {
        this.servers = new ArrayList<>();
        this.eventLoopGropHandler = new EventLoopGropHandler();
    }

    public void addServer(final String name, final String host, final int port, final ServerCodec serverCodec) {
        this.servers.add(new NettyServer(name, host, port, this.eventLoopGropHandler, serverCodec));
    }

    public void start() {
        this.servers.forEach(NettyServer::start);
    }
}
