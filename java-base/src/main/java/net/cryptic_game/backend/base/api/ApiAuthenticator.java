package net.cryptic_game.backend.base.api;

import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;

public interface ApiAuthenticator {

    boolean isPermitted(ApiRequest request, int authentication, ApiEndpointData endpoint);
}
