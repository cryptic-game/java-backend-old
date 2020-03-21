package net.cryptic_game.backend.server.server.daemon;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;

public class DaemonServerCodec extends NettyCodec<DaemonCodecServerInitializer> {

    public DaemonServerCodec(final DaemonEndpointHandler daemonEndpointHandler) {
        super(new DaemonCodecServerInitializer(), new JsonApiServerCodec(new ApiEndpointFinder(daemonEndpointHandler.getApiList()), daemonEndpointHandler.getApiList().getClientList()));
    }
}
