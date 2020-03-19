package net.cryptic_game.backend.server.daemon;

import io.netty.channel.ChannelHandlerContext;

public class Daemon {

    private final ChannelHandlerContext ctx;
    private String name;

    public Daemon(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.ctx;
    }
}
