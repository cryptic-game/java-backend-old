package net.cryptic_game.backend.daemon.client.daemon;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import net.cryptic_game.backend.base.netty.JsonMessageCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;

public class DaemonClientInitializer implements NettyInitializer {
    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new JsonMessageCodec());
        pipeline.addLast(new DaemonClientHandler());
    }
}
