package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Daemon implements JsonSerializable {

    private final Channel channel;
    private final LocalDateTime connectedSince;
    private final String name;

    public Daemon(final Channel channel, final String name) {
        this.channel = channel;
        this.name = name;
        this.connectedSince = LocalDateTime.now();
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("name", this.getName())
                .add("connected_since", this.getConnectedSince().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getConnectedSince() {
        return this.connectedSince;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
