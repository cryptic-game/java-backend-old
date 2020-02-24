package net.cryptic_game.backend.daemon.client.daemon;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;

public class DaemonClientCodec implements NettyCodec {

    private final DaemonClientInitializer initializer;

    public DaemonClientCodec() {
        this.initializer = new DaemonClientInitializer();
    }

    @Override
    public NettyInitializer getInitializer() {
        return this.initializer;
    }
}
