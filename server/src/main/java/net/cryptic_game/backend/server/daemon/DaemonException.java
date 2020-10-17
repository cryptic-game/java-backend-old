package net.cryptic_game.backend.server.daemon;

import java.io.IOException;

public class DaemonException extends IOException {

    public DaemonException(final String message) {
        super(message);
    }

    public DaemonException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
