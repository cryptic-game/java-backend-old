package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiCollection;
import net.cryptic_game.backend.base.api.ApiEndpoint;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.client.Client;
import net.cryptic_game.backend.server.client.ClientWrapper;

import static net.cryptic_game.backend.base.utils.JsonBuilder.simple;
import static net.cryptic_game.backend.base.utils.JsonSocketUtils.build;

public class InfoEndpoints extends ApiCollection {

    @ApiEndpoint("online")
    public JsonObject online(Client client) {
        return build(ResponseType.OK, simple("online", ClientWrapper.getOnlineCount()));
    }

    @ApiEndpoint("info")
    public JsonObject info(Client client) {
        if (client.getUser() == null) return build(ResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        return build(ResponseType.OK, JsonBuilder.anJSON()
                .add("user", client.getUser().serialize()).build());
    }
}
