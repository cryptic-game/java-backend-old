package net.cryptic_game.backend.server.server.http.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.server.http.HttpEndpoint;

public class PlayersOnlineEndpoint extends HttpEndpoint {

    public PlayersOnlineEndpoint() {
        super("online");
    }

    @Override
    public JsonObject handleRequest() {
//        return simple("online", -1);
        return JsonBuilder.simple("work-in-progress", true);
    }
}
