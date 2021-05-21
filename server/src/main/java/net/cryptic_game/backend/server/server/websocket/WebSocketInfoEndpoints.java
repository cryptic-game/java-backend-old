package net.cryptic_game.backend.server.server.websocket;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiRequest;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "info", type = ApiType.WEBSOCKET)
public final class WebSocketInfoEndpoints {

    private final UserRepository userRepository;

    @ApiEndpoint(id = "online")
    public ApiResponse online() {
        return new ApiResponse(HttpResponseStatus.NOT_IMPLEMENTED);
    }

    @ApiEndpoint(id = "info")
    public ApiResponse info(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request) {
        final Optional<User> user = request.getContext().get(User.class);
        if (user.isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        return new ApiResponse(HttpResponseStatus.OK, this.userRepository.findById(user.get().getId()).orElse(null));
    }
}
