package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

public class WebSocketJsonEncoder extends MessageToMessageEncoder<JsonObject> {

    @Override
    protected void encode(final ChannelHandlerContext ctx, final JsonObject msg, final List<Object> out) {
        out.add(new TextWebSocketFrame(msg.toString()));
    }
}
