package net.cryptic_game.backend.base.api.netty.websocket;

import lombok.Data;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocationProvider;

import java.util.Map;

@Data
public final class WebSocketLocationProvider implements HttpLocationProvider<WebSocketLocation> {

    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public WebSocketLocation getLocation() {
        return new WebSocketLocation(this.endpoints);
    }
}
