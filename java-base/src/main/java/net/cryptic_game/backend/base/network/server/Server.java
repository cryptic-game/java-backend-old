package net.cryptic_game.backend.base.network.server;

import java.net.SocketAddress;

public interface Server {

    void start();

    void stop();

    boolean isRunning();

    String getId();

    SocketAddress getAddress();

    default void restart() {
        if (this.isRunning()) this.stop();
        this.start();
    }
}
