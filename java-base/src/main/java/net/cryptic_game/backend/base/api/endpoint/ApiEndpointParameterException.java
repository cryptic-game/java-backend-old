package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.api.ApiException;

public class ApiEndpointParameterException extends ApiException {

    ApiEndpointParameterException(final String message) {
        super(message);
    }
}
