package net.cryptic_game.backend.daemon.client.daemon;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpointFinder;
import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;

public class DaemonClientCodec extends NettyCodec<DaemonClientCodecInitializer> {

    public DaemonClientCodec(final DaemonEndpointHandler endpointHandler) {
        super(new DaemonClientCodecInitializer(), new JsonApiServerCodec(new ApiEndpointFinder(endpointHandler.getApiList()),
                endpointHandler.getApiList().getClientList()));
    }
}
