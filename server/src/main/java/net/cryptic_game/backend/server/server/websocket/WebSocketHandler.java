package net.cryptic_game.backend.server.server.websocket;

import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getString;
import static net.cryptic_game.backend.base.utils.JsonUtils.getUUID;
import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;
import net.cryptic_game.backend.base.api.ApiExecutor;
import net.cryptic_game.backend.base.netty.NettyHandler;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.api.ServerApiExecutionData;
import net.cryptic_game.backend.server.api.ServerApiExecutor;
import net.cryptic_game.backend.server.client.ClientWrapper;
import net.cryptic_game.backend.server.server.ServerResponseType;

public class WebSocketHandler extends NettyHandler<JsonObject> {

  private final ApiExecutor executor;

  public WebSocketHandler() {
    this.executor = new ServerApiExecutor(App.getInstance().getApiHandler());
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JsonObject json) throws Exception {
    String action = getString(json, "action");
    UUID tag = getUUID(json, "tag");
    JsonObject data = getJsonObject(json, "data");

    if (action == null) {
      ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_ACTION"));
      return;
    }

    if (tag == null) {
      ctx.write(build(ServerResponseType.BAD_REQUEST, "MISSING_TAG"));
      return;
    }

    if (data == null) {
      data = new JsonObject();
    }

    JsonObject response = this.executor
        .execute(new ServerApiExecutionData(action, ClientWrapper.getClient(ctx), data));
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
      ctx.write(WebSocketUtils.build(ServerResponseType.BAD_REQUEST, "JSON_SYNTAX_ERROR"));
      return;
    }

    super.exceptionCaught(ctx, cause);
    ctx.write(WebSocketUtils.build(ServerResponseType.INTERNAL_SERVER_ERROR));
  }
}
