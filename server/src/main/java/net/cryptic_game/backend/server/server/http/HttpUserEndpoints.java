package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.Constants;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRepository;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user", type = ApiType.REST)
public final class HttpUserEndpoints {

    private final Key key;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserSuspensionRepository userSuspensionRepository;

    @ApiEndpoint(id = "login")
    public ApiResponse login(@ApiParameter(id = "access_token") final String accessTokenJwt) {

        final JsonObject accessToken;
        try {
            accessToken = SecurityUtils.parseJwt(key, accessTokenJwt);
        } catch (JsonParseException | SignatureException e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_TOKEN");
        } catch (ExpiredJwtException e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
        }

        final UUID userId;
        try {
            userId = UUID.fromString(accessToken.get("user_id").getAsString());
        } catch (Exception e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        }

        if (user.isNewUser()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "USER_REGISTRATION_REQUIRED");
        }

        if (this.userSuspensionRepository.getActiveSuspensionsByUserId(user.getId()).size() > 0) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
        }

        user.setLast(OffsetDateTime.now());
        this.userRepository.save(user);
        final Session session = this.sessionRepository.createSession(user);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("session", session));
    }

    @ApiEndpoint(id = "register")
    public ApiResponse register(@ApiParameter(id = "access_token") final String accessTokenJwt,
                                @ApiParameter(id = "username") final String username) {

        final JsonObject accessToken;
        try {
            accessToken = SecurityUtils.parseJwt(key, accessTokenJwt);
        } catch (JsonParseException | SignatureException e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_TOKEN");
        } catch (ExpiredJwtException e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
        }

        final UUID userId;
        try {
            userId = UUID.fromString(accessToken.get("user_id").getAsString());
        } catch (Exception e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_TOKEN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        }

        if (!user.isNewUser()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "USER_ALREADY_REGISTERED");
        }

        if (!Constants.USERNAME.matcher(username).matches()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_USERNAME");
        }

        if (this.userRepository.findByUsername(username).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "USERNAME_TAKEN");
        }

        user.setUsername(username);
        user.setNewUser(false);

        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("user", this.userRepository.save(user)));
    }
}
