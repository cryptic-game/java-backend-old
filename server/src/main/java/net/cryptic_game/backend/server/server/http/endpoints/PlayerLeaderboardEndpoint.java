package net.cryptic_game.backend.server.server.http.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.server.http.HttpEndpoint;

public class PlayerLeaderboardEndpoint extends HttpEndpoint {

    public PlayerLeaderboardEndpoint() {
        super("leaderboard");
    }

    @Override
    public JsonObject handleRequest() {
//        JSONBuilder jsonBuilder = JSONBuilder.anJSON()
//                .add("ms", "server")
//                .add("endpoint", Collections.singletonList("leaderboard"))
//                .add("tag", UUID.randomUUID().toString());
//        sendRaw(MicroService.get("currency").getChannel(), jsonBuilder.build());
//
//        sendHTTP(channel, simple("users", ));

        return JsonBuilder.simple("work-in-progress", true);
    }
}
