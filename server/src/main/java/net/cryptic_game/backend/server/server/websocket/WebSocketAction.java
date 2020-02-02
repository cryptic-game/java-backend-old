package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;

public abstract class WebSocketAction {

    private final String name;

    public WebSocketAction(final String name) {
        this.name = name;
    }

    public abstract JsonObject handleRequest() throws Exception;

    public String getName() {
        return this.name;
    }
}
