package net.cryptic_game.backend.base.netty.server;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;

@Service
public final class NettyServerService {

    private final Set<NettyServer> servers;

    public NettyServerService() {
        this.servers = new HashSet<>();
    }

    public void addServer(final NettyServer server) {
        this.servers.add(server);
        server.start();
    }

    @PreDestroy
    private void preDestroy() {
        this.servers.forEach(NettyServer::close);
    }
}
