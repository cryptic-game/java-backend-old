package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;

public class WebSocketServerCodecInitializer extends NettyCodecInitializer<WebSocketServerCodec> {

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast("http-server-codec", new HttpServerCodec());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("websocket-server-protocol-handler", new WebSocketServerProtocolHandler("/"));
        pipeline.addLast("websocket-text-frame-codec", new WebsocketTextFrameMessageCodec());
        pipeline.addLast("websocket-ping-pong-handler", new WebSocketPingPongHandler());
    }
}
