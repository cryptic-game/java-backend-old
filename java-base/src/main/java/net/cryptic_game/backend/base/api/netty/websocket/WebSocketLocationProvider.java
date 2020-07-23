package net.cryptic_game.backend.base.api.netty.websocket;

import lombok.Data;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocationProvider;

import java.util.Map;
import java.util.function.Consumer;

@Data
public final class WebSocketLocationProvider implements HttpLocationProvider<WebSocketLocation> {

    private final Map<String, ApiEndpointData> endpoints;
    private final Consumer<ApiClient> clientAddedConsumer;

    @Override
    public WebSocketLocation getLocation() {
        return new WebSocketLocation(this.endpoints, this.clientAddedConsumer);
    }
}
