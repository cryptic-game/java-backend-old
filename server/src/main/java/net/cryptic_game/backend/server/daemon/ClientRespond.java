package net.cryptic_game.backend.server.daemon;

import io.netty.channel.Channel;

public class ClientRespond {

    private final String tag;
    private final Channel channel;

    public ClientRespond(final String tag, final Channel channel) {
        this.tag = tag;
        this.channel = channel;
    }

    public String getTag() {
        return this.tag;
    }

    public Channel getChannel() {
        return this.channel;
    }
}
