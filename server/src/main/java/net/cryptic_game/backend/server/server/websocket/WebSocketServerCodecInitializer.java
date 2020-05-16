package net.cryptic_game.backend.server.server.websocket;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;
import net.cryptic_game.backend.base.netty.NettyCodecInitializer;
import net.cryptic_game.backend.server.App;
import net.cryptic_game.backend.server.server.websocket.playground.PlaygroundContentHandler;

public class WebSocketServerCodecInitializer extends NettyCodecInitializer<WebSocketServerCodec> {

    private final ApiEndpointList endpointList;

    public WebSocketServerCodecInitializer(final ApiEndpointList endpointList) {
        this.endpointList = endpointList;
    }

    @Override
    public void configure(ChannelPipeline pipeline) {
        pipeline.addLast("http-server-codec", new HttpServerCodec());
        pipeline.addLast("chunked-write-handler", new ChunkedWriteHandler());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("websocket-server-protocol-handler", new WebSocketServerProtocolHandler("/"));
        pipeline.addLast("websocket-text-frame-codec", new WebsocketTextFrameMessageCodec());
        if (!App.getInstance().getConfig().isProductive()) {
            pipeline.addLast(new PlaygroundContentHandler(this.endpointList.getPlaygroundContent()));
        }
    }
}
