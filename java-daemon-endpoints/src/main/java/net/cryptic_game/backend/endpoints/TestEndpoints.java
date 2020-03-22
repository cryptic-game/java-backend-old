package net.cryptic_game.backend.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;

public class TestEndpoints extends ApiEndpointCollection {

    public TestEndpoints() {
        super("test");
    }

    @ApiEndpoint("test")
    public ApiResponse test() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }
}
