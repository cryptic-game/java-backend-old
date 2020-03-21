package net.cryptic_game.backend.server.server.http;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;

import java.nio.charset.StandardCharsets;

public class HttpServerCodecInitializer extends NettyCodecInitializer<net.cryptic_game.backend.server.server.http.HttpServerCodec> {

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        pipeline.addLast(new HttpToStringMessageCodec());
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        pipeline.addLast(new StringEncoder(StandardCharsets.UTF_8));
    }
}
