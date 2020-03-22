package net.cryptic_game.backend.base.api.request;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;

import java.util.UUID;

public class ApiRequest {

    public static UUID request(final Channel channel, final String endpoint, final JsonObject data) {
        final UUID tag = UUID.randomUUID();

        final JsonObject json = new JsonObject();
        json.addProperty("tag", tag.toString());
        json.addProperty("endpoint", endpoint);
        if (data != null) json.add("data", data);

        channel.writeAndFlush(json.toString());
        return tag;
    }

    public static UUID request(final Channel channel, final String endpoint) {
        return request(channel, endpoint, null);
    }
}
