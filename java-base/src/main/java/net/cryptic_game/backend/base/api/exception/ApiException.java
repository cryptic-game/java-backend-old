package net.cryptic_game.backend.base.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ApiException extends Exception {

    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
