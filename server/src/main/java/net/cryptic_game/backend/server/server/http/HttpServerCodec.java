package net.cryptic_game.backend.server.server.http;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class HttpServerCodec extends NettyCodec<HttpServerCodecInitializer> {

    public HttpServerCodec(final HttpEndpointHandler httpEndpointHandler) {
        super(new HttpServerCodecInitializer(), new JsonApiServerCodec(new ApiEndpointFinder(httpEndpointHandler.getApiList()), httpEndpointHandler.getApiList().getClientList()));
        this.initializer.setCodec(this);
    }
}
