package net.cryptic_game.backend.base.api.netty;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClientList;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.netty.NettyCodec;

import java.util.function.Consumer;

public class JsonApiServerCodec extends NettyCodec<JsonApiServerCodecInitializer> {

    private final ApiEndpointFinder finder;
    private final ApiClientList clientList;
    private final Consumer<JsonObject> consumer;

    public JsonApiServerCodec(final ApiEndpointFinder finder, final ApiClientList clientList) {
        this(finder, clientList, null);
    }

    public JsonApiServerCodec(final ApiEndpointFinder finder, final ApiClientList clientList, final Consumer<JsonObject> consumer) {
        super(new JsonApiServerCodecInitializer());
        this.initializer.setCodec(this);
        this.finder = finder;
        this.clientList = clientList;
        this.consumer = consumer;
    }

    ApiEndpointFinder getFinder() {
        return this.finder;
    }

    ApiClientList getClientList() {
        return this.clientList;
    }

    Consumer<JsonObject> getConsumer() {
        return this.consumer;
    }
}
