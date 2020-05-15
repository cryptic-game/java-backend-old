package net.cryptic_game.backend.base.daemon;

import io.netty.channel.Channel;
import net.cryptic_game.backend.base.json.JsonTransient;

import java.time.ZonedDateTime;

public class Daemon {

    @JsonTransient
    private final Channel channel;
    private final ZonedDateTime connectedSince;
    private final String name;

    public Daemon(final Channel channel, final String name) {
        this.channel = channel;
        this.name = name;
        this.connectedSince = ZonedDateTime.now();
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
