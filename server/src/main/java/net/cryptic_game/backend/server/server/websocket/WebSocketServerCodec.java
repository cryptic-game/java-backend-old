package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class WebSocketServerCodec extends NettyCodec<WebSocketServerCodecInitializer> {

    public WebSocketServerCodec(final WebSocketEndpointHandler webSocketEndpointHandler) {
        super(new WebSocketServerCodecInitializer(), new JsonApiServerCodec(new ApiEndpointFinder(webSocketEndpointHandler.getApiList()), webSocketEndpointHandler.getApiList().getClientList()));
        this.initializer.setCodec(this);
    }
}
