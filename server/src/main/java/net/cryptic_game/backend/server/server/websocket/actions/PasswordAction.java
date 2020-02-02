package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.base.data.user.UserWrapper;
import net.cryptic_game.backend.base.utils.ValidationUtils;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;

import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class PasswordAction extends WebSocketAction {

    public PasswordAction() {
        super("password");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        if (client.getUser() == null) return build(ServerResponseType.FORBIDDEN, "NOT_LOGGED_IN");
        if (data == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_DATA");

        String password = getString(data, "password");
        String newPassword = getString(data, "new");

        if (password == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_PASSWORD");
        if (newPassword == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_NEW_PASSWORD");
        if (!ValidationUtils.checkPassword(newPassword)) return build(ServerResponseType.BAD_REQUEST, "INVALID_PASSWORD");
        if (!UserWrapper.verifyPassword(client.getUser(), password)) return build(ServerResponseType.UNAUTHORIZED, "INVALID_PASSWORD");

        UserWrapper.setPassword(client.getUser(), newPassword);

        return build(ServerResponseType.OK);
    }
}
