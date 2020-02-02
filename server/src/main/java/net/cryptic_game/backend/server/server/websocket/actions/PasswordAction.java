package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class PasswordAction extends WebSocketAction {

    public PasswordAction() {
        super("password");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        String password = getString(data, "password");
        String newPassword = getString(data, "new");

        if (password == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_PASSWORD");
        if (newPassword == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_NEW_PASSWORD");

        return null;
    }
}
