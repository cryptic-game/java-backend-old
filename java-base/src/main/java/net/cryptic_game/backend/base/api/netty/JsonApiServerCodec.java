package net.cryptic_game.backend.base.api.netty;

import net.cryptic_game.backend.base.api.client.ApiClientList;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class JsonApiServerCodec extends NettyCodec<JsonApiServerCodecInitializer> {

    private final ApiEndpointFinder finder;
    private final ApiClientList clientList;

    public JsonApiServerCodec(final ApiEndpointFinder finder, final ApiClientList clientList) {
        super(new JsonApiServerCodecInitializer());
        this.initializer.setCodec(this);
        this.finder = finder;
        this.clientList = clientList;
    }

    ApiEndpointFinder getFinder() {
        return this.finder;
    }

    ApiClientList getClientList() {
        return this.clientList;
    }
}
