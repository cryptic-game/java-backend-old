package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;

public class WebSocketPingPongHandler extends NettyChannelHandler<PingWebSocketFrame> {

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final PingWebSocketFrame msg) throws Exception {
        ctx.write(new PongWebSocketFrame());
    }
}
