package net.cryptic_game.backend.base.netty.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public final class MessageLoggerCodec extends MessageToMessageCodec<String, String> {

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
