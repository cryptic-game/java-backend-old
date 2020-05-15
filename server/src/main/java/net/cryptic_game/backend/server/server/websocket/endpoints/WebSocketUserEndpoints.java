package net.cryptic_game.backend.server.server.websocket.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.data.user.User;

import java.time.ZonedDateTime;
import java.util.UUID;

public class WebSocketUserEndpoints extends ApiEndpointCollection {

    public WebSocketUserEndpoints() {
        super("user", "todo");
    }

    @ApiEndpoint("login")
    public ApiResponse login(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
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

        final User user = User.getByUsername(username);

        if (user == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_USERNAME");
        }
        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        final UUID token = UUID.randomUUID();
        session = Session.createSession(user, token, deviceName);

        client.add(session);

        Session.deleteExpiredSessions(user);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.create("session", session.getId())
                .add("token", token));
    }

    @ApiEndpoint("register")
    public ApiResponse register(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                                @ApiParameter("username") final String username,
                                @ApiParameter("password") final String password,
                                @ApiParameter("mail") final String mail,
                                @ApiParameter("device_name") final String deviceName) {
        Session session = client.get(Session.class);
        if (session != null && session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (username.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_USERNAME");
        }

        if (!ValidationUtils.checkPassword(password)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        if (!ValidationUtils.checkMail(mail)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_MAIL");
        }

        if (User.getByUsername(username) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");
        }

        final UUID token = UUID.randomUUID();
        final User user = User.createUser(username, mail, password);
        session = Session.createSession(user, token, deviceName);
        client.add(session);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.create("session", session.getId())
                .add("token", token));

    }

    @ApiEndpoint("session")
    public ApiResponse session(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
                               @ApiParameter("session") final UUID sessionId,
                               @ApiParameter("token") final UUID token) {
        Session session = client.get(Session.class);
        if (session != null && session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        session = Session.getById(sessionId);
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
        session.setLastActive(ZonedDateTime.now());
        session.getUser().setLast(ZonedDateTime.now());
        session.getUser().saveOrUpdate();
        session.saveOrUpdate();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("change_password")
    public ApiResponse changePassword(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client,
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
        user.saveOrUpdate();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("logout")
    public ApiResponse logout(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client, @ApiParameter(value = "session", optional = true) final UUID sessionId) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (sessionId == null) {
            session.delete();
            client.remove(Session.class);
            return new ApiResponse(ApiResponseType.OK);
        }

        session = Session.getById(sessionId);

        if (session == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SESSION_NOT_FOUND");
        }

        session.delete();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client, @ApiParameter("password") final String password) {
        Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = client.get(Session.class).getUser();

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        client.remove(Session.class);
        user.delete();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "client", special = ApiParameterSpecialType.CLIENT) final ApiClient client, @ApiParameter("id") final UUID userId) {
        final Session session = client.get(Session.class);
        if (session == null || !session.isValid()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = User.getById(userId);

        if (user == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.create("id", user.getId())
                .add("name", user.getUsername())
                .add("created", user.getCreated())
                .add("last", user.getLast()));
    }
}
