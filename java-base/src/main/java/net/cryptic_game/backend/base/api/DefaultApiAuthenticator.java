package net.cryptic_game.backend.base.api;

import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultApiAuthenticator implements ApiAuthenticator {

    @Override
    public boolean isPermitted(final ApiRequest request, final int authentication, final ApiEndpointData endpoint) {
        return true;
    }
}
