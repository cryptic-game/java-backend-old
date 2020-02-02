package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;

public abstract class WebSocketAction {

    private final String name;

    public WebSocketAction(final String name) {
        this.name = name;
    }

    public abstract JsonObject handleRequest(Client client, JsonObject data) throws Exception;

    public String getName() {
        return this.name;
    }
}
