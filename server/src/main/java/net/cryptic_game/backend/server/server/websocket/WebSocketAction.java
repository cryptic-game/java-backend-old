package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.user.User;

import java.util.UUID;

public abstract class WebSocketAction {

    private final String name;

    public WebSocketAction(final String name) {
        this.name = name;
    }

    public abstract JsonObject handleRequest(User user, UUID tag, JsonObject data) throws Exception;

    public String getName() {
        return this.name;
    }
}
