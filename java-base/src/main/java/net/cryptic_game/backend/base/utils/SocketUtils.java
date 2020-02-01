package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.base.interfaces.Error;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class SocketUtils {

    public static void sendRaw(Channel channel, JsonObject data) {
        channel.writeAndFlush(data.toString());
    }

    public static void sendRaw(Channel channel, Error error) {
        sendRaw(channel, error.getResponse());
    }

    public static void sendWebsocket(Channel channel, JsonObject data) {
        channel.writeAndFlush(new TextWebSocketFrame(data.toString()));
    }

    public static void sendWebsocket(Channel channel, Error error) {
        sendWebsocket(channel, error.getResponse());
    }

    public static void sendHTTP(Channel channel, JsonObject data) {
        final String responseMessage = data.toString();

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                copiedBuffer(responseMessage.getBytes()));

        response.headers().set(HttpHeaderNames.SERVER, "cryptic-server");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseMessage.length());

        channel.writeAndFlush(response);
    }

    public static void sendHTTP(Channel channel, Error error) {
        sendHTTP(channel, error.getResponse());
    }
}
