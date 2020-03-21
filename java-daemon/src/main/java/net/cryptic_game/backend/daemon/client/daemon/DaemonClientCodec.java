package net.cryptic_game.backend.daemon.client.daemon;

import net.cryptic_game.backend.base.netty.NettyCodec;

public class DaemonClientCodec extends NettyCodec<DaemonClientCodecInitializer> {

    public DaemonClientCodec() {
        super(new DaemonClientCodecInitializer());
    }
}
