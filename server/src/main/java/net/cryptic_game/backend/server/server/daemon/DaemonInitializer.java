package net.cryptic_game.backend.server.server.daemon;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonLoggerMessageCodec;
import net.cryptic_game.backend.base.netty.codec.JsonMessageCodec;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.daemon.DaemonHandler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DaemonInitializer implements NettyInitializer {

    private final DaemonHandler daemonHandler;
    private final Map<String, DaemonEndpoint> endpoints;

    public DaemonInitializer(final DaemonHandler daemonHandler, final Map<String, DaemonEndpoint> endpoints) {
        this.daemonHandler = daemonHandler;
        this.endpoints = endpoints;
    }

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new JsonObjectDecoder());
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
        pipeline.addLast(new JsonMessageCodec());
        if (!App.getInstance().getConfig().getAsBoolean(BaseConfig.PRODUCTIVE)) {
            pipeline.addLast(new JsonLoggerMessageCodec());
        }
        pipeline.addLast(new DaemonContentHandler(this.daemonHandler, this.endpoints));
    }
}
