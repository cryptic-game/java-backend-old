package net.cryptic_game.backend.server.server.websocket.actions;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.data.client.Client;
import net.cryptic_game.backend.base.data.session.Session;
import net.cryptic_game.backend.base.data.session.SessionWrapper;
import net.cryptic_game.backend.base.utils.JsonUtils;
import net.cryptic_game.backend.server.server.ServerResponseType;
import net.cryptic_game.backend.server.server.websocket.WebSocketAction;
import net.cryptic_game.backend.server.server.websocket.WebSocketUtils;

import java.util.UUID;

import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class SessionAction extends WebSocketAction {

    public SessionAction() {
        super("session");
    }

    @Override
    public JsonObject handleRequest(Client client, JsonObject data) throws Exception {
        if (client.getUser() != null) return build(ServerResponseType.FORBIDDEN, "ALREADY_LOGGED_IN");
        if (data == null) return build(ServerResponseType.BAD_REQUEST, "MISSING_DATA");

        UUID sessionUuid = JsonUtils.getUUID(data, "session");
        if (sessionUuid == null) return WebSocketUtils.build(ServerResponseType.BAD_REQUEST, "MISSING_SESSION");

        Session session = SessionWrapper.getSessionById(sessionUuid);
        if (session == null) return WebSocketUtils.build(ServerResponseType.NOT_FOUND, "INVALID_SESSION");
        if (!session.isValid()) return WebSocketUtils.build(ServerResponseType.UNAUTHORIZED, "SESSION_EXPIRED");

        client.setSession(session);

        return build(ServerResponseType.OK);
    }
}
