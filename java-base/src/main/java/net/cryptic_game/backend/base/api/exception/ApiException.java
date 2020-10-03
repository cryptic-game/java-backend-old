package net.cryptic_game.backend.base.api.exception;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@NoArgsConstructor
public class ApiException extends IOException {

    public ApiException(@NotNull final String message) {
        super(message);
    }

    public ApiException(@NotNull final String message, @NotNull final Throwable cause) {
        super(message, cause);
    }
}
