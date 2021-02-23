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
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.Constants;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserSuspension;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRevokedRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user", type = ApiType.WEBSOCKET)
public final class WebSocketUserEndpoints {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final UserSuspensionRepository userSuspensionRepository;
    private final UserSuspensionRevokedRepository userSuspensionRevokedRepository;

    @ApiEndpoint(id = "login")
    public ApiResponse login(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                             @ApiParameter(id = "username") final String username,
                             @ApiParameter(id = "password") final String password) {
        if (request.getContext().get(Session.class).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (!Constants.USERNAME.matcher(username).matches()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_USERNAME");
        }

        final User user = this.userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_USERNAME");
        }

        final List<UserSuspension> suspensions = this.userSuspensionRepository.findAllByUserIdAndExpiresAfter(user.getId(), OffsetDateTime.now());

        if (suspensions.size() != 0
                && suspensions.stream().map(userSuspension -> userSuspensionRevokedRepository.findByUserSuspensionId(userSuspension.getId()))
                .anyMatch(Optional::isEmpty)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCOUNT_SUSPENDED");
        }

        if (!user.verifyPassword(password)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        final Session session = sessionRepository.createSession(user);
        request.getContext().set(session);

        return new ApiResponse(HttpResponseStatus.OK, session);
    }

    @ApiEndpoint(id = "register")
    public ApiResponse register(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                                @ApiParameter(id = "username") final String username,
                                @ApiParameter(id = "password") final String password) {
        if (request.getContext().get(Session.class).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (!Constants.USERNAME.matcher(username).matches()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_USERNAME");
        }

        if (!ValidationUtils.checkPassword(password)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_PASSWORD");
        }

        if (this.userRepository.findByUsername(username).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "USER_ALREADY_EXISTS");
        }

        final User user = this.userRepository.createUser(username, password);
        final Session session = this.sessionRepository.createSession(user);
        request.getContext().set(session);

        return new ApiResponse(HttpResponseStatus.OK, session);

    }

    @ApiEndpoint(id = "session")
    public ApiResponse session(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                               @ApiParameter(id = "session") final UUID sessionId,
                               @ApiParameter(id = "user_id") final UUID userId) {
        if (request.getContext().get(Session.class).isPresent()) {
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

        this.sessionRepository.save(session);

        request.getContext().set(session);
        final OffsetDateTime now = OffsetDateTime.now();
        user.setLast(now);
        this.userRepository.save(user);

        return new ApiResponse(HttpResponseStatus.OK, session);
    }

    @ApiEndpoint(id = "change_password")
    public ApiResponse changePassword(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                                      @ApiParameter(id = "password") final String password,
                                      @ApiParameter(id = "new") final String newPassword) {
        final Optional<Session> session = request.getContext().get(Session.class);
        if (session.isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (!ValidationUtils.checkPassword(newPassword)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_PASSWORD");
        }

        final User user = this.userRepository.findById(session.get().getUserId()).orElseThrow();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        user.setPassword(newPassword);
        this.userRepository.save(user);

        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "logout")
    public ApiResponse logout(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                              @ApiParameter(id = "session", required = false) final UUID sessionId) {
        final Optional<Session> session = request.getContext().get(Session.class);
        if (session.isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (sessionId == null) {
            this.sessionRepository.delete(session.get());
            request.getContext().remove(Session.class);
            return new ApiResponse(HttpResponseStatus.OK);
        }

        Session deleteSession = this.sessionRepository.findById(sessionId).orElse(null);

        if (deleteSession == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "SESSION_NOT_FOUND");
        }

        this.sessionRepository.delete(deleteSession);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "delete")
    public ApiResponse delete(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                              @ApiParameter(id = "password") final String password) {
        final Optional<Session> session = request.getContext().get(Session.class);
        if (session.isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = this.userRepository.findById(session.get().getUserId()).orElseThrow();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        request.getContext().remove(Session.class);
        this.userRepository.delete(user);

        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "get")
    public ApiResponse get(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request,
                           @ApiParameter(id = "id") final UUID userId) {
        if (request.getContext().get(Session.class).isEmpty()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);

        if (user == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        return new ApiResponse(HttpResponseStatus.OK, user.serializePublic());
    }
}
