package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "debug", type = ApiType.REST, disabled = true)
public class HttpDebugEndpoints {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @ApiEndpoint(id = "login")
    public ApiResponse login(@ApiParameter(id = "username") final String username) {
        final OffsetDateTime now = OffsetDateTime.now();
        User user = this.userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setCreated(now);
        }
        user.setLast(now);
        this.userRepository.save(user);
        final Session session = this.sessionRepository.createSession(user);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("session", session));
    }
}
