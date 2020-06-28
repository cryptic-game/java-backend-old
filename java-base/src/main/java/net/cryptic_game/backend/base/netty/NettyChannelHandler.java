package net.cryptic_game.backend.base.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class NettyChannelHandler<T> extends SimpleChannelInboundHandler<T> {

    /**
     * Is executes when a packet is on the end of the pipeline.
     *
     * @param ctx the {@link ChannelHandlerContext} of the current pipeline
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * This is executes when an {@link Throwable} in the pipeline occurs.
     *
     * @param ctx   the {@link ChannelHandlerContext} of the current pipeline
     * @param cause the {@link Throwable}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause.getMessage().equals("An existing connection was forcibly closed by the remote host")) {
            log.error(cause.getMessage() + " (" + ctx.channel() + ")");
        } else log.error("Failed to progress channel. \"" + ctx.channel() + "\"", cause);
    }
}
