package net.cryptic_game.backend.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.DaemonUtils;

import java.util.UUID;

public class TestEndpoints extends ApiEndpointCollection {

    public TestEndpoints() {
        super("test");
    }

    @ApiEndpoint("timeout")
    public ApiResponse timeout() {
        try {
            Thread.sleep(1000 * 25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("notification")
    public ApiResponse notification(final ApiClient client, @ApiParameter("user_id") final UUID userId) {
        DaemonUtils.notifyUser(client.getChannel(), userId, "test", JsonBuilder.create("foo", "bar"));
        return new ApiResponse(ApiResponseType.OK);
    }
}
