package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Daemon implements JsonSerializable {

    private final ChannelHandlerContext ctx;
    private final LocalDateTime connectedSince;
    private String name;

    public Daemon(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
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

    public void setName(final String name) {
        this.name = name;
    }

    public LocalDateTime getConnectedSince() {
        return this.connectedSince;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.ctx;
    }
}
