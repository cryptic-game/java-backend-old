package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

public class WebSocketInitializer implements ServerCodecInitializer {

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new HttpResponseEncoder());
        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
        pipeline.addLast(new WebSocketHandler());
    }
}
