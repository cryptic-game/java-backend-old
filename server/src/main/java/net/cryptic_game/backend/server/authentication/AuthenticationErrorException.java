package net.cryptic_game.backend.server.authentication;

import lombok.NoArgsConstructor;
import net.cryptic_game.backend.base.api.exception.ApiException;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor
public final class AuthenticationErrorException extends ApiException {

    public AuthenticationErrorException(final @NotNull String message) {
        super(message);
    }

}
