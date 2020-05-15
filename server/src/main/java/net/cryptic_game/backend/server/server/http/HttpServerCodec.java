package net.cryptic_game.backend.server.server.http;

import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class HttpServerCodec extends NettyCodec<HttpServerCodecInitializer> {

    public HttpServerCodec(final HttpEndpointHandler httpEndpointHandler) {
        super(new HttpServerCodecInitializer(), new JsonApiServerCodec(httpEndpointHandler.getApiList().getEndpoints(), httpEndpointHandler.getApiList().getClients()));
        this.initializer.setCodec(this);
    }
}
