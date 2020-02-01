package net.cryptic_game.backend.server.server;

import io.netty.channel.ChannelPipeline;

public interface ServerCodecInitializer {

    void configure(final ChannelPipeline pipeline);
}
