package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;

@ApiEndpointCollection(id = "info", type = ApiType.REST)
public final class HttpInfoEndpoint {

    @ApiEndpoint(id = "online")
    public ApiResponse online() {
        return new ApiResponse(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @ApiEndpoint(id = "leaderboard")
    public ApiResponse leaderboard() {
        return new ApiResponse(HttpResponseStatus.NOT_IMPLEMENTED);
    }
}
