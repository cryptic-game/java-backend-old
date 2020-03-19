package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.netty.NettyInitializer;
import net.cryptic_game.backend.base.netty.codec.JsonLoggerMessageCodec;
import net.cryptic_game.backend.base.netty.codec.JsonMessageCodec;
import net.cryptic_game.backend.server.App;

public class WebSocketInitializer implements NettyInitializer {

    private static final String WEBSOCKET_PATH = "/";

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast("codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerProtocolHandler(WebSocketInitializer.WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketServerCodec());
        pipeline.addLast(new JsonMessageCodec());
        if (!App.getInstance().getConfig().getAsBoolean(BaseConfig.PRODUCTIVE)) {
            pipeline.addLast(new JsonLoggerMessageCodec());
        }
        pipeline.addLast(new WebSocketHandler());
    }
}
