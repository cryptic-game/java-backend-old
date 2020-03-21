package net.cryptic_game.backend.server.server.http.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.utils.JsonUtils;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

public class HttpInfoEndpoint extends ApiEndpointCollection {

    private final DaemonHandler daemonHandler;

    public HttpInfoEndpoint(final DaemonHandler daemonHandler) {
        super("info");
        this.daemonHandler = daemonHandler;
    }

    @ApiEndpoint("status")
    public ApiResponse status() {
        return new ApiResponse(ApiResponseType.OK, JsonUtils.toArray(this.daemonHandler.getDaemons()));
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("leaderboard")
    public ApiResponse leaderboard() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }
}
