package net.cryptic_game.backend.server.server.deamon;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.server.deamon.DaemonHandler;

public class DaemonCodec implements NettyCodec {

    private final DaemonHandler daemonHandler;

    public DaemonCodec(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    @Override
    public NettyInitializer getInitializer() {
        return new DaemonInitializer(this.daemonHandler);
    }
}
