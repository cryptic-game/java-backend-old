package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.server.server.ServerResponseType;

import java.util.UUID;

public class WebSocketUtils {

    public static JsonObject build(ServerResponseType responseType) {
        return build(responseType, null, null, null);
    }

    public static JsonObject build(ServerResponseType responseType, String errorMessage) {
        return build(responseType, errorMessage, null, null);
    }

    public static JsonObject build(ServerResponseType responseType, String errorMessage, UUID tag) {
        return build(responseType, errorMessage, tag, null);
    }

    public static JsonObject build(ServerResponseType responseType, UUID tag) {
        return build(responseType, null, tag, null);
    }

    public static JsonObject build(ServerResponseType responseType, UUID tag, JsonObject data) {
        return build(responseType, null, tag, data);
    }

    public static JsonObject build(ServerResponseType responseType, JsonObject data) {
        return build(responseType, null, null, data);
    }

    public static JsonObject build(ServerResponseType responseType, String errorMessage, UUID tag,
                                   JsonObject data) {
        JsonObject status = responseType.serialize();

        if (errorMessage != null) {
            status.addProperty("message", errorMessage);
        }

        JsonObject response = JsonBuilder.anJSON()
                .add("status", status).build();

        if (tag != null) {
            response.addProperty("tag", tag.toString());
        }

        if (data != null) {
            response.add("data", data);
        }

        return response;
    }
}
