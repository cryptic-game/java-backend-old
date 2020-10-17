package net.cryptic_game.backend.base.api.handler.rest;

import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;

public interface RestApiAuthenticator extends ApiAuthenticator {

    @Override
    default boolean isPermitted(ApiRequest request, int authentication, ApiEndpointData endpoint) {
        if (request instanceof RestApiRequest) {
            return this.isPermitted((RestApiRequest) request, authentication, endpoint);
        }

        throw new UnsupportedOperationException("not a rest api");
    }

    boolean isPermitted(RestApiRequest request, int authentication, ApiEndpointData endpoint);
}
