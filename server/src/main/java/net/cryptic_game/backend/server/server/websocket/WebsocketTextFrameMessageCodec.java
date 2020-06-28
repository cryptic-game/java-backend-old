package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public final class WebsocketTextFrameMessageCodec extends MessageToMessageCodec<TextWebSocketFrame, String> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final String msg, final List<Object> out) throws Exception {
        out.add(new TextWebSocketFrame(msg));
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final TextWebSocketFrame msg, final List<Object> out) throws Exception {
        out.add(msg.text());
    }
}
