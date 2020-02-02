package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

public class LoginAction extends WebSocketAction {

    public LoginAction() {
        super("login");
    }

    @Override
    public JsonObject handleRequest() throws Exception {
        return null;
    }
}
