package net.cryptic_game.backend.base.api.exception;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ApiException extends IOException {

    public ApiException(@NotNull final String message) {
        super(message);
    }

    public ApiException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
