package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;

public class WebSocketCodec implements NettyCodec {

    private final NettyInitializer initializer;

    public WebSocketCodec() {
        this.initializer = new WebSocketInitializer();
    }


    @Override
    public NettyInitializer getInitializer() {
        return this.initializer;
    }
}
