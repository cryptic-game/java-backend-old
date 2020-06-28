package net.cryptic_game.backend.daemon.client.daemon;

import net.cryptic_game.backend.base.api.netty.JsonApiServerCodec;
import net.cryptic_game.backend.base.netty.NettyCodec;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;

public final class DaemonClientCodec extends NettyCodec<DaemonClientCodecInitializer> {

    public DaemonClientCodec(final DaemonEndpointHandler endpointHandler) {
        super(new DaemonClientCodecInitializer(), new JsonApiServerCodec(endpointHandler.getApiList().getEndpoints(), endpointHandler.getApiList().getClients()));
    }
}
