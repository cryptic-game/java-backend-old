package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.Constants;
import net.cryptic_game.backend.data.redis.entities.Session;
import net.cryptic_game.backend.data.redis.repositories.SessionRepository;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public final class WebSocketUserEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public WebSocketUserEndpoints(final UserRepository userRepository, final SessionRepository sessionRepository) {
        super("user", "todo");
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @ApiEndpoint("login")
    public ApiResponse login(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                             @ApiParameter("username") final String username,
                             @ApiParameter("password") final String password) {
        Session session = client.get(Session.class);
        if (session != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (!Constants.USERNAME.matcher(username).matches()) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_USERNAME");
        }

        final User user = this.userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_USERNAME");
        }
        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        session = sessionRepository.createSession(user);

        client.add(session);

        return new ApiResponse(ApiResponseType.OK, session);
    }

    @ApiEndpoint("register")
    public ApiResponse register(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                @ApiParameter("username") final String username,
                                @ApiParameter("password") final String password) {
        Session session = client.get(Session.class);
        if (session != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (!Constants.USERNAME.matcher(username).matches()) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_USERNAME");
        }

        if (!ValidationUtils.checkPassword(password)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        if (this.userRepository.findByUsername(username).isPresent()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");
        }

        final User user = this.userRepository.createUser(username, password);
        session = this.sessionRepository.createSession(user);
        client.add(session);

        return new ApiResponse(ApiResponseType.OK, session);

    }

    @ApiEndpoint("session")
    public ApiResponse session(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                               @ApiParameter("session") final UUID sessionId,
                               @ApiParameter("user_id") final UUID userId) {
        Session session = client.get(Session.class);
        if (session != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        final User user = this.userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
        }


        session = this.sessionRepository.findById(sessionId).orElse(null);
        if (session == null || !session.getUserId().equals(userId)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVALID_SESSION");
        }

        this.sessionRepository.save(session);

        client.add(session);
        final OffsetDateTime now = OffsetDateTime.now();
        user.setLast(now);
        this.userRepository.save(user);

        return new ApiResponse(ApiResponseType.OK, session);
    }

    @ApiEndpoint("change_password")
    public ApiResponse changePassword(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                      @ApiParameter("password") final String password,
                                      @ApiParameter("new") final String newPassword) {
        Session session = client.get(Session.class);
        if (session == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (!ValidationUtils.checkPassword(newPassword)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        final User user = this.userRepository.findById(session.getUserId()).orElseThrow();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        user.setPassword(newPassword);
        this.userRepository.save(user);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("logout")
    public ApiResponse logout(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                              @ApiParameter(value = "session", optional = true) final UUID sessionId) {
        Session session = client.get(Session.class);
        if (session == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (sessionId == null) {
            //session.delete(sqlSession);
            client.remove(Session.class);
            return new ApiResponse(ApiResponseType.OK);
        }

        //session = Session.getById(sqlSession, sessionId);

        if (session == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SESSION_NOT_FOUND");
        }

        // session.delete(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                              @ApiParameter("password") final String password) {
        Session session = client.get(Session.class);
        if (session == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = this.userRepository.findById(session.getUserId()).orElseThrow();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        client.remove(Session.class);
        //user.delete(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                           @ApiParameter("id") final UUID userId) {
        final Session session = client.get(Session.class);
        if (session == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        //TODO
        final User user = null;

        if (user == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, user.serializePublic());
    }
}
