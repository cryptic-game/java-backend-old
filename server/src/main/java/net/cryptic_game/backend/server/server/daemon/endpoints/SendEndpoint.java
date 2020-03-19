package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpoint;

import java.util.UUID;

import static net.cryptic_game.backend.base.utils.JsonSocketUtils.build;
import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getUUID;

public class SendEndpoint extends DaemonEndpoint {

    public SendEndpoint() {
        super("send");
    }

    @Override
    public JsonObject handleRequest(JsonObject data) throws Exception {
        UUID user = getUUID(data, "user");
        JsonObject userData = getJsonObject(data, "data");

        if (user == null) {
            return build(ResponseType.BAD_REQUEST, "MISSING_USER");
        }

        if (userData == null) {
            return build(ResponseType.BAD_REQUEST, "MISSING_USER_DATA");
        }

        return null;
    }
}
