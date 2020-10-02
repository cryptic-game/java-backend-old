package net.cryptic_game.backend.base.api.exception;

import org.jetbrains.annotations.NotNull;

public class ApiInternalParameterException extends ApiParameterException {

    public ApiInternalParameterException(@NotNull final String message) {
        super(message);
    }

    public ApiInternalParameterException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
