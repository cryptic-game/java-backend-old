package net.cryptic_game.backend.server.daemon;

import io.netty.channel.Channel;

class ClientRespond {

    private final String tag;
    private final Channel channel;

    ClientRespond(final String tag, final Channel channel) {
        this.tag = tag;
        this.channel = channel;
    }

    String getTag() {
        return this.tag;
    }

    Channel getChannel() {
        return this.channel;
    }
}
