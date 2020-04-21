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
        try {
            Thread.sleep(1000 * 25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("timeout")
    public ApiResponse timeout() {
        try {
            Thread.sleep(1000 * 25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }
}
