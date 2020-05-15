package net.cryptic_game.backend.server.server.daemon;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class DaemonServerCodec extends NettyCodec<DaemonCodecServerInitializer> {

    private final DaemonHandler daemonHandler;

    public DaemonServerCodec(final DaemonHandler daemonHandler, final DaemonEndpointHandler daemonEndpointHandler) {
        super(new DaemonCodecServerInitializer());
        this.daemonHandler = daemonHandler;
        this.setChildCodecs(new JsonApiServerCodec(daemonEndpointHandler.getApiList().getEndpoints(),
                daemonEndpointHandler.getApiList().getClients(),
                this::handleResponse,
                null,
                (apiClient -> daemonHandler.removeDaemon(apiClient.getChannel()))));
    }

    private void handleResponse(final JsonObject jsonObject, final ApiClient client) {
        final String tag = JsonUtils.fromJson(jsonObject.get("tag"), String.class);
        if (this.daemonHandler.isRequestOpen(tag)) this.daemonHandler.respondToClient(jsonObject);
        else client.get(Daemon.class).getChannel()
                .writeAndFlush(JsonBuilder.create("tag", tag)
                        .add("info", JsonBuilder.create(ApiResponseType.BAD_REQUEST).add("message", "TOO_LATE"))
                        .add("response", true)
                        .build());
    }
}
