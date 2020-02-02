package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.server.server.ServerCodec;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;
import net.cryptic_game.backend.server.server.websocket.actions.LoginAction;

import java.util.HashMap;
import java.util.Map;

public class WebSocketCodec implements ServerCodec {

    private final ServerCodecInitializer initializer;
    private final Map<String, WebSocketAction> actions;

    public WebSocketCodec() {
        this.actions = new HashMap<>();
        this.initializer = new WebSocketInitializer(this.actions);

        this.addAction(new LoginAction());
    }

    private void addAction(WebSocketAction action) {
        this.actions.put(action.getName().toLowerCase(), action);
    }

    @Override
    public ServerCodecInitializer getInitializer() {
        return this.initializer;
    }
}
