package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.SocketUtils;
import net.cryptic_game.backend.server.server.NettyHandler;
import net.cryptic_game.backend.server.server.ServerResponseType;

import java.util.Map;
import java.util.UUID;

import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getString;

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

    private JsonObject build(ServerResponseType responseType) {
        return build(responseType, null, null, null);
    }

    private JsonObject build(ServerResponseType responseType, String errorMessage) {
        return build(responseType, errorMessage, null, null);
    }

    private JsonObject build(ServerResponseType responseType, String errorMessage, UUID tag) {
        return build(responseType, errorMessage, tag, null);
    }

    private JsonObject build(ServerResponseType responseType, UUID tag) {
        return build(responseType, null, tag, null);
    }

    private JsonObject build(ServerResponseType responseType, UUID tag, JsonObject data) {
        return build(responseType, null, tag, data);
    }

    private JsonObject build(ServerResponseType responseType, String errorMessage, UUID tag, JsonObject data) {
        JsonObject status = responseType.serialize();

        if (errorMessage != null)
            status.addProperty("message", errorMessage);

        JsonObject response = JsonBuilder.anJSON()
                .add("status", status).build();

        if (tag != null)
            response.addProperty("tag", tag.toString());

        if (data != null)
            response.add("data", data);

        return response;
    }
}
