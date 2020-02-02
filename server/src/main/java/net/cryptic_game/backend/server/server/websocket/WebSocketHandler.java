package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.server.server.NettyHandler;
import net.cryptic_game.backend.server.server.ServerResponseType;

import java.util.Map;

import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class WebSocketHandler extends NettyHandler<JsonObject> {

    private final Map<String, WebSocketAction> actions;

    public WebSocketHandler(Map<String, WebSocketAction> actions) {
        this.actions = actions;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonObject json) throws Exception {
        String actionName = getString(json, "action");
        String tag = getString(json, "tag");
        JsonObject data = getJsonObject(json, "data");

        if (actionName == null) {
            ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_ACTION"));
            return;
        }

        if (tag == null) {
            ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_TAG"));
            return;
        }

        if (!this.actions.containsKey(actionName)) {
            ctx.write(build(ServerResponseType.NOT_FOUND, "UNKNOWN_ACTION"));
            return;
        }
    }
}
