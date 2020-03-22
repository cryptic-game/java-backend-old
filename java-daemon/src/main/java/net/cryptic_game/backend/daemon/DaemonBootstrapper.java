package net.cryptic_game.backend.daemon;

import io.netty.channel.Channel;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointExecutor;
import net.cryptic_game.backend.base.daemon.DaemonRegisterPacket;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.base.daemon.FunctionArgument;
import net.cryptic_game.backend.daemon.api.DaemonEndpointHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DaemonBootstrapper {

    private final DaemonRegisterPacket drp;
    private final DaemonEndpointHandler daemonEndpointHandler;

    public DaemonBootstrapper(final String name, final DaemonEndpointHandler daemonEndpointHandler) {
        this.drp = new DaemonRegisterPacket(name);
        this.daemonEndpointHandler = daemonEndpointHandler;
    }

    void sendRegisterPackage(final Channel channel) {
        this.loadFunctions();
        channel.writeAndFlush(this.drp.serialize());
    }

    private void loadFunctions() {
        final HashMap<String, ApiEndpointExecutor> apiExecutors = this.daemonEndpointHandler.getApiList().getApiExecutors();
        apiExecutors.forEach((name, executor) -> {
            final Set<FunctionArgument> arguments = new HashSet<>();
            executor.getParameters().forEach(parameter -> arguments.add(new FunctionArgument(parameter.getKey(), !parameter.isOptional())));
            this.drp.addFunction(new Function(name, arguments));
        });
    }
}
