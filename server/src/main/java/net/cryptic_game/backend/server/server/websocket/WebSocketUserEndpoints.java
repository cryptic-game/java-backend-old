package net.cryptic_game.backend.server.server.websocket;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.entities.user.Session;
import net.cryptic_game.backend.data.entities.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class WebSocketUserEndpoints extends ApiEndpointCollection {

    public WebSocketUserEndpoints() {
        super("user", "todo");
    }

    @ApiEndpoint("login")
    public ApiResponse login(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                             @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,
                             @ApiParameter("username") final String username,
                             @ApiParameter("password") final String password,
                             @ApiParameter("device_name") final String deviceName) {
        Session session = client.get(Session.class);
        if (session != null && session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (username.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_USERNAME");
        }

        final User user = User.getByUsername(sqlSession, username);

        if (user == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_USERNAME");
        }
        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        final UUID token = UUID.randomUUID();
        session = Session.createSession(sqlSession, user, token, deviceName);

        client.add(session);

        Session.deleteExpiredSessions(sqlSession, user);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.create("session", session.getId())
                .add("token", token));
    }

    @ApiEndpoint("register")
    public ApiResponse register(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,
                                @ApiParameter("username") final String username,
                                @ApiParameter("password") final String password,
                                @ApiParameter("device_name") final String deviceName) {
        Session session = client.get(Session.class);
        if (session != null && session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (!username.matches("^[a-zA-Z0-9\\-_.]{2,32}$")) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_USERNAME");
        }

        if (!ValidationUtils.checkPassword(password)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        if (User.getByUsername(sqlSession, username) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");
        }

        final UUID token = UUID.randomUUID();
        final User user = User.createUser(sqlSession, username, password);
        session = Session.createSession(sqlSession, user, token, deviceName);
        client.add(session);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.create("session", session.getId())
                .add("token", token));

    }

    @ApiEndpoint("session")
    public ApiResponse session(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                               @ApiParameter(value = "sqlSession", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,
                               @ApiParameter("session") final UUID sessionId,
                               @ApiParameter("token") final UUID token) {
        Session session = client.get(Session.class);
        if (session != null && session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        session = Session.getById(sqlSession, sessionId);
        if (session == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVALID_SESSION");
        }
        if (!token.equals(session.getToken())) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_SESSION_TOKEN");
        }
        if (!session.isValid()) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "SESSION_EXPIRED");
        }

        client.add(session);
        final OffsetDateTime now = OffsetDateTime.now();
        session.setLastActive(now);
        session.getUser().setLast(now);
        session.getUser().saveOrUpdate(sqlSession);
        session.saveOrUpdate(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("change_password")
    public ApiResponse changePassword(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                      @ApiParameter(value = "sqlSession", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,
                                      @ApiParameter("password") final String password,
                                      @ApiParameter("new") final String newPassword) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (!ValidationUtils.checkPassword(newPassword)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        final User user = session.getUser();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        user.setPassword(newPassword);
        user.saveOrUpdate(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("logout")
    public ApiResponse logout(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                              @ApiParameter(value = "sqlSession", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,

                              @ApiParameter(value = "session", optional = true) final UUID sessionId) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (sessionId == null) {
            session.delete(sqlSession);
            client.remove(Session.class);
            return new ApiResponse(ApiResponseType.OK);
        }

        session = Session.getById(sqlSession, sessionId);

        if (session == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SESSION_NOT_FOUND");
        }

        session.delete(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                              @ApiParameter(value = "sqlSession", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final org.hibernate.Session sqlSession,

                              @ApiParameter("password") final String password) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = client.get(Session.class).getUser();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        client.remove(Session.class);
        user.delete(sqlSession);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                           @ApiParameter(value = "sqlSession", special = ApiParameterSpecialType.SQL_SESSION) final org.hibernate.Session sqlSession,
                           @ApiParameter("id") final UUID userId) {
        final Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = User.getById(sqlSession, userId);

        if (user == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, user.serializePublic());
    }
}
