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
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user", type = ApiType.WEBSOCKET)
public final class WebSocketUserEndpoints {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @ApiEndpoint(id = "session")
    public ApiResponse session(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                               @ApiParameter(id = "session_id") final UUID sessionId,
                               @ApiParameter(id = "user_id") final UUID userId) {
        if (request.getContext().get(User.class).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        final Session session = this.sessionRepository.findById(sessionId).orElse(null);
        if (session == null || !session.getUserId().equals(userId)) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "INVALID_SESSION");
        }

        this.sessionRepository.delete(session);
        request.getContext().set(user);

        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("session", session).add("user", user));
    }

    @ApiEndpoint(id = "get")
    public ApiResponse get(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                           @ApiParameter(id = "id") final UUID userId) {
        if (request.getContext().get(User.class).isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        return new ApiResponse(HttpResponseStatus.OK, user.serializePublic());
    }
}
