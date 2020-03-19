package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpoint;

import java.util.UUID;

public class RespondEndpoint extends DaemonEndpoint {

    public RespondEndpoint() {
        super("respond");
    }

    @Override
    public JsonObject handleRequest(final UUID tag, final JsonObject data) throws Exception {
        return null;
    }
}
