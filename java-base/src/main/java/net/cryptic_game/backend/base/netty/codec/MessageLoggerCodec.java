package net.cryptic_game.backend.base.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class MessageLoggerCodec extends MessageToMessageCodec<String, String> {

    private static final Logger log = LoggerFactory.getLogger(MessageLoggerCodec.class);

    @Override
    protected void decode(final ChannelHandlerContext ctx, final String msg, final List<Object> out) throws Exception {
        final String from = ctx.channel().remoteAddress() == null ? ctx.channel().id().toString() : ctx.channel().remoteAddress().toString();
        log.info("Received from " + from + ": " + msg);
        out.add(msg);
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final String msg, final List<Object> out) throws Exception {
        final String to = ctx.channel().remoteAddress() == null ? ctx.channel().id().toString() : ctx.channel().remoteAddress().toString();
        log.info("Sent to " + to + ": " + msg);
        out.add(msg);
    }
}
