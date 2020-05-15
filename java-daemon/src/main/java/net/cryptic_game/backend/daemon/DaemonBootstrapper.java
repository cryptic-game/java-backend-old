package net.cryptic_game.backend.daemon;

import io.netty.channel.Channel;
import net.cryptic_game.backend.base.daemon.DaemonRegisterPacket;
import net.cryptic_game.backend.base.utils.ApiUtils;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;

public class DaemonBootstrapper {

    private final DaemonRegisterPacket drp;
    private final DaemonEndpointHandler daemonEndpointHandler;

    public DaemonBootstrapper(final String name, final DaemonEndpointHandler daemonEndpointHandler) {
        this.drp = new DaemonRegisterPacket(name);
        this.daemonEndpointHandler = daemonEndpointHandler;
    }

    void sendRegisterPackage(final Channel channel) {
        this.drp.getEndpointCollections().clear();
        this.drp.addEndpointCollections(this.daemonEndpointHandler.getApiList().getCollections().values());
        ApiUtils.request(channel, "daemon/register", this.drp);
    }
}
