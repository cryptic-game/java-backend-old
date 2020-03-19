package net.cryptic_game.backend.server.server.http.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.JsonUtils;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.http.HttpEndpoint;

public class StatusEndpoint extends HttpEndpoint {

    private final DaemonHandler daemonHandler;

    public StatusEndpoint(final DaemonHandler daemonHandler) {
        super("status");
        this.daemonHandler = daemonHandler;
    }

    @Override
    public JsonObject handleRequest() throws Exception {
        return JsonBuilder.anJSON()
                .add("daemons", JsonUtils.toArray(this.daemonHandler.getDaemons()))
                .build();
    }
}
