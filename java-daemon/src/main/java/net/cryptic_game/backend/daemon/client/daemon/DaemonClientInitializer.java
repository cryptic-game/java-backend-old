package net.cryptic_game.backend.daemon.client.daemon;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.cryptic_game.backend.base.netty.JsonMessageCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;

import java.nio.charset.StandardCharsets;

public class DaemonClientInitializer implements NettyInitializer {
    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
        pipeline.addLast(new JsonMessageCodec());
        pipeline.addLast(new DaemonClientHandler());
    }
}
