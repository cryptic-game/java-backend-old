package net.cryptic_game.backend.base.api.request;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import java.util.UUID;

public class ApiRequest {

    public static UUID request(final Channel channel, final String endpoint, final JsonObject data) {
        final UUID tag = UUID.randomUUID();

        final JsonBuilder builder = JsonBuilder.anJSON()
                .add("tag", tag.toString())
                .add("endpoint", endpoint)
                .add("response", false);
        if (data != null) builder.add("data", data);

        channel.writeAndFlush(builder.build());
        return tag;
    }

    public static UUID request(final Channel channel, final String endpoint) {
        return request(channel, endpoint, null);
    }

    public static void response(final Channel channel, final UUID tag, final JsonObject data) {
        final JsonBuilder builder = JsonBuilder.anJSON()
                .add("tag", tag.toString())
                .add("response", true);
        if (data != null) builder.add("data", data);

        channel.writeAndFlush(builder.build());
    }
}
