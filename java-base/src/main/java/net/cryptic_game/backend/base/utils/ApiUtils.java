package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.json.JsonBuilder;

import java.util.UUID;

public class ApiUtils {

    public static UUID request(final Channel channel, final String endpoint, final JsonObject data) {
        final UUID tag = UUID.randomUUID();

        final JsonBuilder builder = JsonBuilder.create("tag", tag.toString())
                .add("endpoint", endpoint)
                .add("response", false)
                .add("data", data != null, () -> data);

        channel.writeAndFlush(builder.build());
        return tag;
    }

    public static UUID request(final Channel channel, final String endpoint) {
        return request(channel, endpoint, null);
    }

    public static void response(final Channel channel, final String tag, final JsonObject info, final JsonElement data) {
        final JsonBuilder builder = JsonBuilder.create("tag", tag)
                .add("info", info)
                .add("response", true)
                .add("data", data != null, () -> data);

        channel.writeAndFlush(builder.build());
    }
}
