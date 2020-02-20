package net.cryptic_game.backend.server.daemon;

import io.netty.channel.ChannelHandlerContext;

public class Daemon {

    private final String name;
    private final ChannelHandlerContext ctx;

    public Daemon(final String name, final ChannelHandlerContext ctx) {
        this.name = name;
        this.ctx = ctx;
    }

    public String getName() {
        return this.name;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return this.ctx;
    }
}
