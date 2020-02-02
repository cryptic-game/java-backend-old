package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.server.server.ServerResponseType;

import java.util.List;

import static net.cryptic_game.backend.base.utils.SocketUtils.sendWebsocket;

public class WebSocketJsonDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {

    @Override
    protected void decode(ChannelHandlerContext ctx, TextWebSocketFrame msg, List<Object> out) throws Exception {
        try {
            out.add(JsonParser.parseString(msg.text()));
        } catch (JsonParseException ignored) {
            sendWebsocket(ctx.channel(), ServerResponseType.JSON_SYNTAX_ERROR);
        }
    }
}
