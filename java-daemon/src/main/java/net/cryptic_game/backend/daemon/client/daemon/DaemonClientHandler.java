package net.cryptic_game.backend.daemon.client.daemon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.netty.NettyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaemonClientHandler extends NettyHandler<JsonElement> {

    private static final Logger log = LoggerFactory.getLogger(DaemonClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JsonElement msg) throws Exception {
        final JsonObject json = new JsonObject();
        json.addProperty("test", 1);
        if (msg instanceof JsonObject && !msg.equals(json)) ctx.write(json);
        else log.info(msg.toString());
    }
}
