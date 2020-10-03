package net.cryptic_game.backend.admin.authentication;

import lombok.NoArgsConstructor;
import net.cryptic_game.backend.base.api.exception.ApiException;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public class AuthenticationErrorException extends ApiException {

    public AuthenticationErrorException(@NotNull String message) {
        super(message);
    }

    public AuthenticationErrorException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
