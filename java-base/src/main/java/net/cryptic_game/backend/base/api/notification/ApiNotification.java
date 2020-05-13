package net.cryptic_game.backend.base.api.notification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Collection;

public class ApiNotification implements JsonSerializable {

    private final String topic;
    private final JsonElement data;

    public ApiNotification(final String topic, final Object object) {
        this.topic = topic;
        this.data = JsonUtils.toJson(object);
    }

    public void send(final Channel channel) {
        channel.writeAndFlush(this.serialize());
    }

    public void send(final Collection<Channel> channels) {
        channels.forEach((channel -> channel.writeAndFlush(this.serialize())));
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("info", JsonBuilder.create("notification", true).add("topic", this.topic))
                .add("data", this.data).build();
    }
}
