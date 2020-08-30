package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.entities.user.Session;

public final class WebSocketInfoEndpoints extends ApiEndpointCollection {

    public WebSocketInfoEndpoints() {
        super("info", "todo");
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client) {
        final Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        return new ApiResponse(ApiResponseType.OK, session.getUser());
    }
}
