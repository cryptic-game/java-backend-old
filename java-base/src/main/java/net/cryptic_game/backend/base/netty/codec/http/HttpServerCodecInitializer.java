package net.cryptic_game.backend.base.netty.codec.http;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerKeepAliveHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.netty.codec.NettyCodecInitializer;

import java.util.Map;

@RequiredArgsConstructor
class HttpServerCodecInitializer implements NettyCodecInitializer {

    private final Map<String, HttpLocationProvider<?>> locationProviders;

    @Override
    public void configure(final ChannelPipeline pipeline) {
        pipeline.addLast("http-server-codec", new HttpServerCodec())
                .addLast("http-server-keepalive-handler", new HttpServerKeepAliveHandler())
                .addLast("chunked-write-handler", new ChunkedWriteHandler())
                .addLast("http-server-content-decoder", new HttpServerContentDecoder(this.locationProviders));
    }
}
