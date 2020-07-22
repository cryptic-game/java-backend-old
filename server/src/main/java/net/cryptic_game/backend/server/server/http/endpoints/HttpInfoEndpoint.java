package net.cryptic_game.backend.server.server.http.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;

public final class HttpInfoEndpoint extends ApiEndpointCollection {

    public HttpInfoEndpoint() {
        super("info", "todo");
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("leaderboard")
    public ApiResponse leaderboard() {
        return new ApiResponse(ApiResponseType.OK);
    }
}
