package net.cryptic_game.backend.base.netty.codec;

import com.google.gson.JsonElement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class JsonLoggerMessageCodec extends MessageToMessageCodec<JsonElement, JsonElement> {

    private static final Logger log = LoggerFactory.getLogger(JsonLoggerMessageCodec.class);

    @Override
    protected void encode(final ChannelHandlerContext ctx, final JsonElement msg, final List<Object> out) throws Exception {
        log.info("[" + ctx.name() + "] Sent to " + ctx.channel().remoteAddress() + ": " + msg.toString());
        out.add(msg);
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final JsonElement msg, final List<Object> out) throws Exception {
        log.info("[" + ctx.name() + "] Received from " + ctx.channel().remoteAddress() + ": " + msg.toString());
        out.add(msg);
    }
}
