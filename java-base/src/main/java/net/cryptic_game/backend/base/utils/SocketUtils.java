package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.base.interfaces.ResponseType;

import java.util.UUID;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class SocketUtils {

    public static void sendRaw(Channel channel, JsonObject data) {
        channel.writeAndFlush(data.toString());
    }

    public static void sendRaw(Channel channel, ResponseType responseType, UUID tag, JsonObject data) {
        sendRaw(channel, JsonBuilder.anJSON()
                .add("status", responseType.serialize())
                .add("tag", tag.toString())
                .add("data", data)
                .build());
    }

    public static void sendRawError(Channel channel, ResponseType responseType, String errorMessage) {
        JsonObject status = responseType.serialize();
        status.addProperty("message", errorMessage);
        sendRaw(channel, JsonBuilder.anJSON()
                .add("status", status)
                .build());
    }

    public static void sendRawError(Channel channel, ResponseType responseType, String errorMessage, UUID tag) {
        JsonObject status = responseType.serialize();
        status.addProperty("message", errorMessage);
        sendRaw(channel, JsonBuilder.anJSON()
                .add("status", status)
                .add("tag", tag.toString())
                .build());
    }

    public static void sendWebsocket(Channel channel, JsonObject data) {
        channel.writeAndFlush(new TextWebSocketFrame(data.toString()));
    }

    public static void sendWebsocket(Channel channel, ResponseType responseType, UUID tag, JsonObject data) {
        sendWebsocket(channel, JsonBuilder.anJSON()
                .add("status", responseType.serialize())
                .add("tag", tag.toString())
                .add("data", data)
                .build());
    }

    public static void sendWebsocketError(Channel channel, ResponseType responseType, String errorMessage) {
        JsonObject status = responseType.serialize();
        status.addProperty("message", errorMessage);
        sendWebsocket(channel, JsonBuilder.anJSON()
                .add("status", status)
                .build());
    }

    public static void sendWebsocketError(Channel channel, ResponseType responseType, String errorMessage, UUID tag) {
        JsonObject status = responseType.serialize();
        status.addProperty("message", errorMessage);
        sendWebsocket(channel, JsonBuilder.anJSON()
                .add("status", status)
                .add("tag", tag.toString())
                .build());
    }
}
