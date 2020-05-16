package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class WebSocketServerCodec extends NettyCodec<WebSocketServerCodecInitializer> {

    public WebSocketServerCodec(final WebSocketEndpointHandler webSocketEndpointHandler) {
        super(new WebSocketServerCodecInitializer(webSocketEndpointHandler.getApiList()), new JsonApiServerCodec(webSocketEndpointHandler.getApiList().getEndpoints(), webSocketEndpointHandler.getApiList().getClients()));
        this.initializer.setCodec(this);
    }
}
