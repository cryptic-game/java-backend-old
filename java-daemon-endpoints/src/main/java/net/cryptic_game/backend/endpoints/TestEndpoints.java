package net.cryptic_game.backend.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.ApiUtils;

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
    public ApiResponse notification(ApiClient client, @ApiParameter("user_id") UUID userId) {
        ApiUtils.request(client.getChannel(), "user/send", JsonBuilder.create("user_id", userId).add("topic", "test").add("data", new JsonObject()).build());
        return new ApiResponse(ApiResponseType.OK);
    }
}
