package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiCollection;
import net.cryptic_game.backend.base.api.ApiEndpoint;
import net.cryptic_game.backend.base.api.ApiParameter;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.session.Session;
import net.cryptic_game.backend.data.session.SessionWrapper;
import net.cryptic_game.backend.data.user.User;
import net.cryptic_game.backend.data.user.UserWrapper;
import net.cryptic_game.backend.server.client.Client;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketUtils;

import java.util.UUID;

import static net.cryptic_game.backend.base.utils.ValidationUtils.checkMail;
import static net.cryptic_game.backend.base.utils.ValidationUtils.checkPassword;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class UserEndpoints extends ApiCollection {

    @ApiEndpoint("login")
    public JsonObject login(Client client,
                            @ApiParameter("name") String name,
                            @ApiParameter("password") String password,
                            @ApiParameter("device_name") String deviceName) {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        User user = UserWrapper.getByName(name);

        if (user == null) return build(ServerResponseType.UNAUTHORIZED, "INVALID_NAME");
        if (!UserWrapper.verifyPassword(user, password))
            return build(ServerResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        UUID token = UUID.randomUUID();
        client.setSession(user, token, deviceName);

        return build(ServerResponseType.OK, JsonBuilder.anJSON()
                .add("session", client.getSession().getId())
                .add("token", token).build());
    }

    @ApiEndpoint("register")
    public JsonObject register(Client client,
                               @ApiParameter("name") String name,
                               @ApiParameter("password") String password,
                               @ApiParameter("mail") String mail,
                               @ApiParameter("device_name") String deviceName) {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        if (!checkPassword(password)) return build(ServerResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if (!checkMail(mail)) return build(ServerResponseType.BAD_REQUEST, "INVALID_MAIL");
        if (UserWrapper.getByName(name) != null) return build(ServerResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");

        User user = UserWrapper.registerUser(name, mail, password);
        UUID token = UUID.randomUUID();
        client.setSession(user, token, deviceName);

        return build(ServerResponseType.OK, JsonBuilder.anJSON()
                .add("session", client.getSession().getId())
                .add("token", token).build());

    }

    @ApiEndpoint("session")
    public JsonObject session(Client client,
                              @ApiParameter("session") UUID sessionId,
                              @ApiParameter("token") UUID token) {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        Session session = SessionWrapper.getSessionById(sessionId);
        if (session == null) return WebSocketUtils.build(ServerResponseType.NOT_FOUND, "INVALID_SESSION");
        if (!SecurityUtils.verify(token.toString(), session.getTokenHash()))
            return WebSocketUtils.build(ServerResponseType.UNAUTHORIZED, "INVALID_SESSION_TOKEN");
        if (!session.isValid()) return WebSocketUtils.build(ServerResponseType.UNAUTHORIZED, "SESSION_EXPIRED");

        client.setSession(session);

        return build(ServerResponseType.OK);
    }

    @ApiEndpoint("password")
    public JsonObject password(Client client,
                               @ApiParameter("password") String password,
                               @ApiParameter("new") String newPassword) {
        if (client.getUser() == null) return build(ServerResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (!ValidationUtils.checkPassword(newPassword))
            return build(ServerResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if (!UserWrapper.verifyPassword(client.getUser(), password))
            return build(ServerResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        UserWrapper.setPassword(client.getUser(), newPassword);

        return build(ServerResponseType.OK);
    }

    @ApiEndpoint("logout")
    public JsonObject logout(Client client,
                             @ApiParameter(value = "session", optional = true) UUID sessionId) {
        if (client.getUser() == null) return build(ServerResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (sessionId == null) {
            client.logout();
            return build(ServerResponseType.OK);
        }

        Session session = SessionWrapper.getSessionById(sessionId);

        if (session == null) return build(ServerResponseType.NOT_FOUND, "SESSION_NOT_FOUND");

        if (client.getSession().equals(session))
            client.logout();
        else
            SessionWrapper.closeSession(session);

        return build(ServerResponseType.OK);
    }

    @ApiEndpoint("delete")
    public JsonObject delete(Client client,
                             @ApiParameter("password") String password) {
        if (client.getUser() == null) return build(ServerResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (!UserWrapper.verifyPassword(client.getUser(), password))
            return build(ServerResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        User user = client.getUser();
        client.logout();
        UserWrapper.deleteUser(user);

        return build(ServerResponseType.OK);
    }
}
