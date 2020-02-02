package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.data.user.UserWrapper;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.base.utils.ValidationUtils.checkMail;
import static net.cryptic_game.backend.base.utils.ValidationUtils.checkPassword;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class RegisterAction extends WebSocketAction {

    public RegisterAction() {
        super("register");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        String name = getString(data, "name");
        String password = getString(data, "password");
        String mail = getString(data, "mail");
        String deviceName = getString(data, "device_name");

        if (name == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_NAME");
        if (password == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_PASSWORD");
        if (mail == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_MAIL");
        if (deviceName == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_DEVICE_NAME");
        if(!checkPassword(password)) return build(ServerResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if(!checkMail(mail)) return build(ServerResponseType.BAD_REQUEST, "INVALID_MAIL");
        if (UserWrapper.getByName(name) != null) return build(ServerResponseType.FORBIDDEN, "USER_ALREADY_EXISTS");

        User user = UserWrapper.register(name, mail, password);
        client.setSession(user, deviceName);

        return build(ServerResponseType.OK, JsonBuilder.simple("session", client.getSession().getId().toString()));
    }
}
