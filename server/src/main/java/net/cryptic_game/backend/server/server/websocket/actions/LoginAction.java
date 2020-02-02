package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.base.data.user.UserWrapper;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class LoginAction extends WebSocketAction {

    public LoginAction() {
        super("login");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");

        String name = getString(data, "name");
        String password = getString(data, "password");
        String deviceName = getString(data, "device_name");

        if (name == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_NAME");
        if (password == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_PASSWORD");
        if (deviceName == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_DEVICE_NAME");

        User user = UserWrapper.getByName(name);

        if (user == null) return build(ServerResponseType.UNAUTHORIZED, "INVALID_NAME");
        if (!UserWrapper.verifyPassword(user, password)) return build(ServerResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        client.setSession(user, deviceName);

        return build(ServerResponseType.OK, JsonBuilder.simple("session", client.getSession().getId().toString()));
    }
}
