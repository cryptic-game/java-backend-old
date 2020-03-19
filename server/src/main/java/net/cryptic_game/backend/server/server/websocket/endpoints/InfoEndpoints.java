package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiCollection;
import net.cryptic_game.backend.base.api.ApiEndpoint;
import net.cryptic_game.backend.base.netty.server.ServerResponseType;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.client.Client;
import net.cryptic_game.backend.server.client.ClientWrapper;

import static net.cryptic_game.backend.base.utils.JsonBuilder.simple;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class InfoEndpoints extends ApiCollection {

    @ApiEndpoint("online")
    public JsonObject online(Client client) {
        return build(ServerResponseType.OK, simple("online", ClientWrapper.getOnlineCount()));
    }

    @ApiEndpoint("info")
    public JsonObject info(Client client) {
        if (client.getUser() == null) return build(ServerResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        return build(ServerResponseType.OK, JsonBuilder.anJSON()
                .add("user", client.getUser().serialize()).build());
    }
}
