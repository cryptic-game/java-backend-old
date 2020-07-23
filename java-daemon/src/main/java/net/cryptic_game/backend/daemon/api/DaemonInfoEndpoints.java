package net.cryptic_game.backend.daemon.api;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;

public final class DaemonInfoEndpoints extends ApiEndpointCollection {

    private final DaemonEndpointHandler daemonEndpointHandler;

    public DaemonInfoEndpoints(final DaemonEndpointHandler daemonEndpointHandler) {
        super("daemon", "Informational endpoints about the daemon.");
        this.daemonEndpointHandler = daemonEndpointHandler;
    }

    @ApiEndpoint(value = "endpoints", description = "All available endpoints of the daemon")
    public ApiResponse endpoints() {
        return new ApiResponse(ApiResponseType.OK, this.daemonEndpointHandler.getApiList().getCollections().values());
    }
}
