package net.cryptic_game.backend.endpoints;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.DaemonUtils;

import java.util.UUID;

@ApiEndpointCollection(id = "test", description = "Some endpoint for testing existing and new features.", disabled = true, type = ApiType.REST)
public final class TestEndpoints {

    @ApiEndpoint(id = "timeout")
    public ApiResponse timeout() {
        try {
            Thread.sleep(1000 * 25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "notification")
    public ApiResponse notification(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId) {
        DaemonUtils.notifyUser(userId, "test", JsonBuilder.create("foo", "bar"));
        return new ApiResponse(HttpResponseStatus.OK);
    }
}
