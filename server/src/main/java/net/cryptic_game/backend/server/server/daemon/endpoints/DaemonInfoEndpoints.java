package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.daemon.DaemonRegisterPacket;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

@Slf4j
public final class DaemonInfoEndpoints extends ApiEndpointCollection {

    private final DaemonHandler daemonHandler;

    public DaemonInfoEndpoints(final DaemonHandler daemonHandler) {
        super("daemon", "todo");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("register")
    public ApiResponse register(
            @ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
            @ApiParameter("name") final String name,
            @ApiParameter("collections") final JsonArray collections) {

        final DaemonRegisterPacket drp = new DaemonRegisterPacket(client.getChannel(), name, collections);
        this.daemonHandler.addDaemon(drp.getDaemon());
        this.daemonHandler.addEndpointCollections(drp.getCollections());

        client.add(drp.getDaemon());

        log.info("Daemon \"" + name + "\" registered.");

        return new ApiResponse(ApiResponseType.OK);
    }
}
