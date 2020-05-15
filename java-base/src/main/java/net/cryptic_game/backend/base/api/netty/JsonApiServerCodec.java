package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointData;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class JsonApiServerCodec extends NettyCodec<JsonApiServerCodecInitializer> {

    public JsonApiServerCodec(final Map<String, ApiEndpointData> endpoints, final Set<ApiClient> clients) {
        this(endpoints, clients, null);
    }

    public JsonApiServerCodec(final Map<String, ApiEndpointData> endpoints,
                              final Set<ApiClient> clients,
                              final BiConsumer<JsonObject, ApiClient> responseCallback) {
        this(endpoints, clients, responseCallback, null, null);
    }

    public JsonApiServerCodec(final Map<String, ApiEndpointData> endpoints,
                              final Set<ApiClient> clients,
                              final Consumer<ApiClient> connectedCallback,
                              final Consumer<ApiClient> disconnectedCallback) {
        this(endpoints, clients, null, connectedCallback, disconnectedCallback);
    }

    public JsonApiServerCodec(final Map<String, ApiEndpointData> endpoints,
                              final Set<ApiClient> clients,
                              final BiConsumer<JsonObject, ApiClient> responseCallback,
                              final Consumer<ApiClient> connectedCallback,
                              final Consumer<ApiClient> disconnectedCallback) {
        super(new JsonApiServerCodecInitializer(endpoints, clients, responseCallback, connectedCallback, disconnectedCallback));
        this.initializer.setCodec(this);
    }
}
