package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.api.ApiExecutor;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.base.utils.JsonSocketUtils;
import net.cryptic_game.backend.base.utils.JsonUtils;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.api.ServerApiExecutionData;
import net.cryptic_game.backend.server.api.ServerApiExecutor;
import net.cryptic_game.backend.server.client.ClientWrapper;

import java.util.UUID;

public class WebSocketHandler extends NettyHandler<JsonObject> {

    private final ApiExecutor executor;

    public WebSocketHandler() {
        this.executor = new ServerApiExecutor(App.getInstance().getApiHandler());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonObject json) throws Exception {
        final String endpoint = JsonUtils.getString(json, "endpoint");
        final UUID tag = JsonUtils.getUUID(json, "tag");
        JsonObject data = JsonUtils.getJsonObject(json, "data");

        if (endpoint == null) {
            ctx.write(JsonSocketUtils.build(ResponseType.BAD_REQUEST, "MISSING_ACTION"));
            return;
        }

        if (tag == null) {
            ctx.write(JsonSocketUtils.build(ResponseType.BAD_REQUEST, "MISSING_TAG"));
            return;
        }

        if (data == null) data = new JsonObject();

        JsonObject response = this.executor.execute(new ServerApiExecutionData(endpoint, ClientWrapper.getClient(ctx), data));
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getCause() instanceof JsonSyntaxException) {
            ctx.write(JsonSocketUtils.build(ResponseType.BAD_REQUEST, "JSON_SYNTAX_ERROR"));
            return;
        }

        super.exceptionCaught(ctx, cause);
        ctx.write(JsonSocketUtils.build(ResponseType.INTERNAL_SERVER_ERROR));
    }
}
