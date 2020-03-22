package net.cryptic_game.backend.server.server.websocket.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.data.user.User;
import net.cryptic_game.backend.server.client.ClientWrapper;

import static net.cryptic_game.backend.base.utils.JsonBuilder.simple;

public class WebSocketInfoEndpoints extends ApiEndpointCollection {

    public WebSocketInfoEndpoints() {
        super("info");
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.OK, simple("online", ClientWrapper.getOnlineCount()));
    }

    @ApiEndpoint("info")
    public ApiResponse info(final ApiClient client) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        return new ApiResponse(ApiResponseType.OK, client.get(User.class));
    }
}
