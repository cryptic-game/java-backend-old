package net.cryptic_game.backend.base.api.data;

import net.cryptic_game.backend.base.api.ApiAuthenticator;

import java.util.function.Function;

public interface AuthenticatorSupplier extends Function<Class<? extends ApiAuthenticator>, ApiAuthenticator> {
}
