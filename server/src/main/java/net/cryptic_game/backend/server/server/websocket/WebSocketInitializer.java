package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.server.ServerCodecInitializer;

public class WebSocketInitializer implements ServerCodecInitializer {

    private static final String WEBSOCKET_PATH = "/";

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler(WebSocketInitializer.WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketJsonDecoder());
        pipeline.addLast(new WebSocketJsonEncoder());
        if (!App.getInstance().getConfig().getAsBoolean(BaseConfig.PRODUCTIVE)) pipeline.addLast(new WebSocketLogger());
        pipeline.addLast(new WebSocketHandler());
    }
}
