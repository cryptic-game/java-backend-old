package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.base.data.client.ClientWrapper;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

import static net.cryptic_game.backend.base.utils.JsonBuilder.simple;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class StatusAction extends WebSocketAction {

    public StatusAction() {
        super("status");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        if (client.getUser() == null) return build(ServerResponseType.OK, simple("online", ClientWrapper.getOnlineCount()));
        JsonObject response = client.getUser().serialize();
        response.addProperty("online", ClientWrapper.getOnlineCount());
        return build(ServerResponseType.OK, response);
    }
}
