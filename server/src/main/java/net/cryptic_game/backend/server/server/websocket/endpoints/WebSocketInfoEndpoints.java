package net.cryptic_game.backend.server.server.websocket.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.server.App;

import static net.cryptic_game.backend.base.json.JsonBuilder.create;

public class WebSocketInfoEndpoints extends ApiEndpointCollection {

    public WebSocketInfoEndpoints() {
        super("info");
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.OK, create("online", ((App) App.getInstance()).getWebSocketEndpointHandler().getApiList().getClientList()
                .filter((apiClient -> apiClient.get(Session.class) != null)).size()));
    }

    @ApiEndpoint("info")
    public ApiResponse info(final ApiClient client) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        return new ApiResponse(ApiResponseType.OK, session.getUser());
    }
}
