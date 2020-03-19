package net.cryptic_game.backend.server.server.websocket.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiCollection;
import net.cryptic_game.backend.base.api.ApiEndpoint;
import net.cryptic_game.backend.base.api.ApiParameter;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.JsonSocketUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.data.user.User;
import net.cryptic_game.backend.server.client.Client;

import java.util.UUID;

import static net.cryptic_game.backend.base.utils.JsonSocketUtils.build;
import static net.cryptic_game.backend.base.utils.ValidationUtils.checkMail;
import static net.cryptic_game.backend.base.utils.ValidationUtils.checkPassword;

public class UserEndpoints extends ApiCollection {

    @ApiEndpoint("login")
    public JsonObject login(Client client,
                            @ApiParameter("name") String name,
                            @ApiParameter("password") String password,
                            @ApiParameter("device_name") String deviceName) {
        if (client.getUser() != null) return build(ResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        User user = User.getByName(name);

        if (user == null) return build(ResponseType.UNAUTHORIZED, "INVALID_NAME");
        if (!user.verifyPassword(password))
            return build(ResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        UUID token = UUID.randomUUID();
        client.setSession(user, token, deviceName);

        return build(ResponseType.OK, JsonBuilder.anJSON()
                .add("session", client.getSession().getId())
                .add("token", token).build());
    }

    @ApiEndpoint("register")
    public JsonObject register(Client client,
                               @ApiParameter("name") String name,
                               @ApiParameter("password") String password,
                               @ApiParameter("mail") String mail,
                               @ApiParameter("device_name") String deviceName) {
        if (client.getUser() != null) return build(ResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        if (!checkPassword(password)) return build(ResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if (!checkMail(mail)) return build(ResponseType.BAD_REQUEST, "INVALID_MAIL");
        if (User.getByName(name) != null) return build(ResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");

        User user = User.createUser(name, mail, password);
        UUID token = UUID.randomUUID();
        client.setSession(user, token, deviceName);

        return build(ResponseType.OK, JsonBuilder.anJSON()
                .add("session", client.getSession().getId())
                .add("token", token).build());

    }

    @ApiEndpoint("session")
    public JsonObject session(Client client,
                              @ApiParameter("session") UUID sessionId,
                              @ApiParameter("token") UUID token) {
        if (client.getUser() != null) return build(ResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        Session session = Session.getById(sessionId);
        if (session == null) return JsonSocketUtils.build(ResponseType.NOT_FOUND, "INVALID_SESSION");
        if (!SecurityUtils.verify(token.toString(), session.getTokenHash()))
            return JsonSocketUtils.build(ResponseType.UNAUTHORIZED, "INVALID_SESSION_TOKEN");
        if (!session.isValid()) return JsonSocketUtils.build(ResponseType.UNAUTHORIZED, "SESSION_EXPIRED");

        client.setSession(session);

        return build(ResponseType.OK);
    }

    @ApiEndpoint("password")
    public JsonObject password(Client client,
                               @ApiParameter("password") String password,
                               @ApiParameter("new") String newPassword) {
        if (client.getUser() == null) return build(ResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (!ValidationUtils.checkPassword(newPassword))
            return build(ResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if (!client.getUser().verifyPassword(password))
            return build(ResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        client.getUser().setPassword(newPassword);
        client.getUser().update();

        return build(ResponseType.OK);
    }

    @ApiEndpoint("logout")
    public JsonObject logout(Client client,
                             @ApiParameter(value = "session", optional = true) UUID sessionId) {
        if (client.getUser() == null) return build(ResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (sessionId == null) {
            client.logout();
            return build(ResponseType.OK);
        }

        Session session = Session.getById(sessionId);

        if (session == null) return build(ResponseType.NOT_FOUND, "SESSION_NOT_FOUND");

        if (client.getSession().equals(session))
            client.logout();
        else {
            session.setValid(false);
            session.update();
        }

        return build(ResponseType.OK);
    }

    @ApiEndpoint("delete")
    public JsonObject delete(Client client,
                             @ApiParameter("password") String password) {
        if (client.getUser() == null) return build(ResponseType.FORBIDDEN, "NOT_LOGGED_IN");

        if (!client.getUser().verifyPassword(password))
            return build(ResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        User user = client.getUser();
        client.logout();
        client.getUser().delete();

        return build(ResponseType.OK);
    }
}
