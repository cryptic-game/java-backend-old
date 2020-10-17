package net.cryptic_game.backend.base.api.handler.websocket;

import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;

public interface WebsocketApiAuthenticator extends ApiAuthenticator {

    @Override
    default boolean isPermitted(ApiRequest request, int authentication, ApiEndpointData endpoint) {
        if (request instanceof WebsocketApiRequest) {
            return this.isPermitted((WebsocketApiRequest) request, authentication, endpoint);
        }

        throw new UnsupportedOperationException("not a websocket");
    }

    boolean isPermitted(WebsocketApiRequest request, int authentication, ApiEndpointData endpoint);
}
