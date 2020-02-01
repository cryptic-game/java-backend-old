package net.cryptic_game.backend.server.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;
    private final ServerCodecInitializer serverCodecInitializer;

    NettyServerInitializer(final SslContext sslContext, final ServerCodecInitializer serverCodecInitializer) throws IllegalArgumentException {
        this.sslContext = sslContext;
        this.serverCodecInitializer = serverCodecInitializer;
    }

    @Override
    protected void initChannel(final SocketChannel channel) {
        final ChannelPipeline pipeline = channel.pipeline();

        if (this.sslContext != null) pipeline.addLast("ssl", this.sslContext.newHandler(channel.alloc()));
        serverCodecInitializer.configure(pipeline);
    }
}
