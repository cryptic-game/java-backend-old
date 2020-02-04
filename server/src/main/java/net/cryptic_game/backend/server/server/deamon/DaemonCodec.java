package net.cryptic_game.backend.server.server.deamon;

import net.cryptic_game.backend.server.deamon.DaemonHandler;
import net.cryptic_game.backend.server.server.ServerCodec;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

public class DaemonCodec implements ServerCodec {

    private final DaemonHandler daemonHandler;

    public DaemonCodec(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    @Override
    public ServerCodecInitializer getInitializer() {
        return new DaemonInitializer(this.daemonHandler);
    }
}
