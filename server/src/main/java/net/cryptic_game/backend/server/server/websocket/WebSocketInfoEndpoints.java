package net.cryptic_game.backend.server.server.websocket;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "info", apiType = ApiType.WEBSOCKET)
public final class WebSocketInfoEndpoints {

    private final UserRepository userRepository;

    @ApiEndpoint(id = "online")
    public ApiResponse online() {
        return new ApiResponse(ApiResponseStatus.NOT_IMPLEMENTED);
    }

//    TODO
//    @ApiEndpoint(id = "info")
//    public ApiResponse info(@ApiParameter(value = "client", special = ApiParameterType.CLIENT) final ApiClient client) {
//        final Session session = client.get(Session.class);
//        if (session == null) {
//            return new ApiResponse(ApiResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
//        }
//
//        return new ApiResponse(ApiResponseStatus.OK, userRepository.findById(session.getUserId()).orElse(null));
//    }
}
