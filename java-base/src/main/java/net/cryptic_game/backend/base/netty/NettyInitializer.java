package net.cryptic_game.backend.base.netty;

import io.netty.channel.ChannelPipeline;

public interface NettyInitializer {

    void configure(final ChannelPipeline pipeline);
}
