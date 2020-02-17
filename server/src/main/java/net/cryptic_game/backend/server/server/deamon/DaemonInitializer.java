package net.cryptic_game.backend.server.server.deamon;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.cryptic_game.backend.base.netty.JsonMessageCodec;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.server.deamon.DaemonHandler;

import java.nio.charset.StandardCharsets;

public class DaemonInitializer implements NettyInitializer {

    private final DaemonHandler daemonHandler;

    public DaemonInitializer(final DaemonHandler daemonHandler) {
        this.daemonHandler = daemonHandler;
    }

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
        pipeline.addLast(new JsonMessageCodec());
        pipeline.addLast(new DaemonContentHandler(this.daemonHandler));
    }
}
