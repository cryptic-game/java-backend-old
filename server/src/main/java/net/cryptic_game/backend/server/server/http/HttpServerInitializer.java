package net.cryptic_game.backend.server.server.http;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

import java.util.Map;

public class HttpServerInitializer implements ServerCodecInitializer {

    private final Map<String, HttpEndpoint> endpoints;

    public HttpServerInitializer(final Map<String, HttpEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        pipeline.addLast(new HttpServerHandler(this.endpoints));
    }
}
