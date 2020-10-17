package net.cryptic_game.backend;

import io.netty.handler.codec.http.HttpHeaderNames;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.handler.rest.RestApiAuthenticator;
import net.cryptic_game.backend.base.api.handler.rest.RestApiRequest;
import org.springframework.stereotype.Component;

@Component
public final class DaemonAuthenticator implements RestApiAuthenticator {

    private final BaseConfig config;
    private final boolean authentication;

    public DaemonAuthenticator(final BaseConfig config) {
        this.config = config;
        this.authentication = this.config.getApiToken() != null && !this.config.getApiToken().isBlank();
    }

    @Override
    public boolean isPermitted(final RestApiRequest request, final int authentication, final ApiEndpointData endpoint) {
        return !this.authentication || this.config.getApiToken().equals(request.getHttpRequest().requestHeaders().get(HttpHeaderNames.AUTHORIZATION));
    }
}
