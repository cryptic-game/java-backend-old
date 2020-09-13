package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.stereotype.Component;

@Component
public final class WebSocketInfoEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;

    public WebSocketInfoEndpoints(final UserRepository userRepository) {
        super("info", "todo");
        this.userRepository = userRepository;
    }

    @ApiEndpoint("online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client) {
        final Session session = client.get(Session.class);
        if (session == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        return new ApiResponse(ApiResponseType.OK, userRepository.findById(session.getUserId()).orElse(null));
    }
}
