package net.cryptic_game.backend.base.api;

import java.io.IOException;

public class ApiException extends IOException {

    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
