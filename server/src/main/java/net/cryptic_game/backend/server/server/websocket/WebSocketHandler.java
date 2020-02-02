package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.data.client.ClientWrapper;
import net.cryptic_game.backend.base.data.user.User;
import net.cryptic_game.backend.server.server.NettyHandler;
import net.cryptic_game.backend.server.server.ServerResponseType;

import java.util.Map;
import java.util.UUID;

import static net.cryptic_game.backend.base.utils.JsonUtils.*;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public class WebSocketHandler extends NettyHandler<JsonObject> {

    private final Map<String, WebSocketAction> actions;

    public WebSocketHandler(Map<String, WebSocketAction> actions) {
        this.actions = actions;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonObject json) throws Exception {
        String actionName = getString(json, "action");
        UUID tag = getUUID(json, "tag");
        JsonObject data = getJsonObject(json, "data");

        if (actionName == null) {
            ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_ACTION"));
            return;
        }

        actionName = actionName.toLowerCase().strip();

        if (tag == null) {
            ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_TAG"));
            return;
        }

        if (!this.actions.containsKey(actionName)) {
            ctx.write(build(ServerResponseType.NOT_FOUND, "UNKNOWN_ACTION"));
            return;
        }

        JsonObject response = this.actions.get(actionName).handleRequest(ClientWrapper.getClient(ctx), data);
        response.addProperty("tag", tag.toString());
        ctx.write(response);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ClientWrapper.addClient(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ClientWrapper.removeClient(ClientWrapper.getClient(ctx));
    }
}
