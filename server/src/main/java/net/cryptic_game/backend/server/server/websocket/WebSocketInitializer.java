package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

import java.util.Map;

public class WebSocketInitializer implements ServerCodecInitializer {

    private static final String WEBSOCKET_PATH = "/";
    private final Map<String, WebSocketAction> actions;

    public WebSocketInitializer(Map<String, WebSocketAction> actions) {
        this.actions = actions;
    }

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler(WebSocketInitializer.WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketJsonDecoder());
        pipeline.addLast(new WebSocketJsonEncoder());
        pipeline.addLast(new WebSocketHandler(this.actions));
    }
}
