package net.cryptic_game.backend.base.api.exception;

import org.jetbrains.annotations.NotNull;

public class ApiParameterException extends ApiException {

    public ApiParameterException(@NotNull final String message) {
        super(message);
    }

    public ApiParameterException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
