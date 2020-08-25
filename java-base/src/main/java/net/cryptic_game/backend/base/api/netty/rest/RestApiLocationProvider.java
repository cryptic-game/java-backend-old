package net.cryptic_game.backend.base.api.netty.rest;

import lombok.Data;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocationProvider;

import java.util.Map;

@Data
public final class RestApiLocationProvider implements HttpLocationProvider<RestApiLocation> {

    private final Map<String, ApiEndpointData> endpoints;
    private final String apiToken;

    @Override
    public RestApiLocation getLocation() {
        return new RestApiLocation(this.endpoints, apiToken);
    }
}
