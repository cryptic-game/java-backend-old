package net.cryptic_game.backend.base.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NettyChannelHandler<T> extends SimpleChannelInboundHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(NettyChannelHandler.class);

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host"))
            log.error(cause.getMessage() + " (" + ctx.channel() + ")");
        else log.error("Failed to progress channel. \"" + ctx.channel() + "\"", cause);
    }
}
