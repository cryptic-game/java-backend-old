package net.cryptic_game.backend.server.server;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.cryptic_game.backend.server.server.websocket.WebSocketUtils.build;

public abstract class NettyHandler<T> extends SimpleChannelInboundHandler<T> {

    private static final Logger log = LoggerFactory.getLogger(NettyHandler.class);

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        if (cause.getCause() instanceof JsonSyntaxException) {
            ctx.write(build(ServerResponseType.BAD_REQUEST, "JSON_SYNTAX_ERROR"));
            return;
        }

        log.error("Failed to progress channel. \"" + ctx.channel() + "\"", cause);
        ctx.write(build(ServerResponseType.INTERNAL_SERVER_ERROR));
    }
}
