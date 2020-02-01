package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.server.server.ServerCodec;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

public class WebSocketCodec implements ServerCodec {

    private final ServerCodecInitializer initializer;

    public WebSocketCodec() {
        this.initializer = new WebSocketInitializer();
    }

    @Override
    public ServerCodecInitializer getInitializer() {
        return this.initializer;
    }
}
