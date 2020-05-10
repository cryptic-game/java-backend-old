package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.time.ZonedDateTime;

public class Daemon implements JsonSerializable {

    private final Channel channel;
    private final ZonedDateTime connectedSince;
    private final String name;

    public Daemon(final Channel channel, final String name) {
        this.channel = channel;
        this.name = name;
        this.connectedSince = ZonedDateTime.now();
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("name", this.getName())
                .add("connected_since", this.getConnectedSince().toInstant())
                .build();
    }

    public String getName() {
        return this.name;
    }

    public ZonedDateTime getConnectedSince() {
        return this.connectedSince;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
