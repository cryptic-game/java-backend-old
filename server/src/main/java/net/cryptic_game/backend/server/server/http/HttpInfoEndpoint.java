package net.cryptic_game.backend.server.server.http;

import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.data.ApiType;

@ApiEndpointCollection(id = "info", apiType = ApiType.REST)
public final class HttpInfoEndpoint {

    @ApiEndpoint(id = "online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseStatus.NOT_IMPLEMENTED);
    }

    @ApiEndpoint(id = "leaderboard")
    public ApiResponse leaderboard() {
        return new ApiResponse(ApiResponseStatus.NOT_IMPLEMENTED);
    }
}
