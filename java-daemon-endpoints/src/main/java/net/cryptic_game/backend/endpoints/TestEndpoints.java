package net.cryptic_game.backend.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.DaemonUtils;

import java.util.UUID;

public final class TestEndpoints extends ApiEndpointCollection {

    public TestEndpoints() {
        super("test", "todo");
    }

    /*@ApiEndpoint("timeout")
    public ApiResponse timeout() {
        try {
            Thread.sleep(1000 * 25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ApiResponseType.OK);
    }*/

    @ApiEndpoint("notification")
    public ApiResponse notification(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                    @ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId) {
        DaemonUtils.notifyUser(client.getChannel(), userId, "test", JsonBuilder.create("foo", "bar"));
        return new ApiResponse(ApiResponseType.OK);
    }
}
