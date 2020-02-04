package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class WebSocketLogger extends MessageToMessageCodec<JsonElement, JsonElement> {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketLogger.class);

    @Override
    protected void encode(final ChannelHandlerContext ctx, final JsonElement msg, final List<Object> out) throws Exception {
        logger.info("Sent to " + ctx.channel().id() + ": " + msg.toString());
        out.add(msg);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final JsonElement msg, final List<Object> out) throws Exception {
        logger.info("Recieved from " + ctx.channel().id() + ": " + msg.toString());
        out.add(msg);
    }
}
