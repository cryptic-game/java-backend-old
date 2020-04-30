package net.cryptic_game.backend.server.server.websocket.endpoints;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.data.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class WebSocketUserEndpoints extends ApiEndpointCollection {

    public WebSocketUserEndpoints() {
        super("user");
    }

    @ApiEndpoint("login")
    public ApiResponse login(final ApiClient client,
                             @ApiParameter("name") final String name,
                             @ApiParameter("password") final String password,
                             @ApiParameter("device_name") final String deviceName) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (name.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_NAME");
        }

        final User user = User.getByName(name);

        if (user == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_NAME");
        }
        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        final UUID token = UUID.randomUUID();
        final Session session = Session.createSession(user, token, deviceName);

        client.add(session);

        Session.deleteExpiredSessions(user);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.anJSON()
                .add("session", session.getId())
                .add("token", token));
    }

    @ApiEndpoint("register")
    public ApiResponse register(final ApiClient client,
                                @ApiParameter("name") final String name,
                                @ApiParameter("password") final String password,
                                @ApiParameter("mail") final String mail,
                                @ApiParameter("device_name") final String deviceName) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        if (name.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_NAME");
        }

        if (!ValidationUtils.checkPassword(password)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        if (!ValidationUtils.checkMail(mail)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_MAIL");
        }

        if (User.getByName(name) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");
        }

        final UUID token = UUID.randomUUID();
        final User user = User.createUser(name, mail, password);
        final Session session = Session.createSession(user, token, deviceName);
        client.add(session);

        return new ApiResponse(ApiResponseType.OK, JsonBuilder.anJSON()
                .add("session", session.getId())
                .add("token", token));

    }

    @ApiEndpoint("session")
    public ApiResponse session(final ApiClient client,
                               @ApiParameter("session") final UUID sessionId,
                               @ApiParameter("token") final UUID token) {

        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        }

        Session session = Session.getById(sessionId);
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
        session.setLastActive(LocalDateTime.now());
        session.getUser().setLast(LocalDateTime.now());
        session.getUser().saveOrUpdate();
        session.saveOrUpdate();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("change_password")
    public ApiResponse changePassword(final ApiClient client,
                                      @ApiParameter("password") final String password,
                                      @ApiParameter("new") final String newPassword) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (!ValidationUtils.checkPassword(newPassword)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        }

        final User user = client.get(User.class);

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        user.setPassword(newPassword);
        user.saveOrUpdate();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("logout")
    public ApiResponse logout(final ApiClient client, @ApiParameter(value = "session", optional = true) final UUID sessionId) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        if (sessionId == null) {
            Session session = client.get(Session.class);
            session.delete();
            return new ApiResponse(ApiResponseType.OK);
        }

        Session session = Session.getById(sessionId);

        if (session == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SESSION_NOT_FOUND");
        }

        session.delete();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(final ApiClient client, @ApiParameter("password") final String password) {
        if (client.get(Session.class) != null || client.get(User.class) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        }

        final User user = client.get(User.class);

        if (!user.verifyPassword(password)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "INVALID_PASSWORD");
        }

        Session session = client.get(Session.class);
        session.delete();

        user.delete();

        return new ApiResponse(ApiResponseType.OK);
    }
}
