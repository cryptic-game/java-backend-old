package net.cryptic_game.backend.server.server.daemon.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;

public class DaemonUserEndpoints extends ApiEndpointCollection {

    public DaemonUserEndpoints() {
        super("user");
    }

    @ApiEndpoint("send")
    public ApiResponse send() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("respond")
    public ApiResponse respond() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }
}
