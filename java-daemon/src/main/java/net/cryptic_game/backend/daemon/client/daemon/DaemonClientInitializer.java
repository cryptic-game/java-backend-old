package net.cryptic_game.backend.daemon.client.daemon;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonLoggerMessageCodec;
import net.cryptic_game.backend.base.netty.codec.JsonMessageCodec;
import net.cryptic_game.backend.daemon.App;

import java.nio.charset.StandardCharsets;

public class DaemonClientInitializer implements NettyInitializer {

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
        pipeline.addLast(new JsonMessageCodec());
        if (!App.getInstance().getConfig().getAsBoolean(BaseConfig.PRODUCTIVE)) {
            pipeline.addLast(new JsonLoggerMessageCodec());
        }
        pipeline.addLast(new DaemonClientHandler());
    }
}
