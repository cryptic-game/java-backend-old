package net.cryptic_game.backend.base.utils;

import io.netty.channel.Channel;
import net.cryptic_game.backend.base.json.JsonBuilder;

import java.util.UUID;

public class DaemonUtils {

    public static void notifyUser(final Channel channel, final UUID user, final String topic, final Object data) {
        ApiUtils.request(channel, "user/send", JsonBuilder.create("user_id", user)
                .add("topic", topic).add("data", data).build());
    }
}
