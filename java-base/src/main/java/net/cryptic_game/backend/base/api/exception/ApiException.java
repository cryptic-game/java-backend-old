package net.cryptic_game.backend.base.api.exception;

import lombok.NoArgsConstructor;

import java.io.IOException;

@NoArgsConstructor
public class ApiException extends IOException {

    public ApiException(final String message) {
        super(message);
    }

    public ApiException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
