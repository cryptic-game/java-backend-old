package net.cryptic_game.backend.base.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;

public class NettyCodecInitializer extends ChannelInitializer<Channel> {

    private final SslContext sslContext;
    private final NettyInitializer nettyInitializer;

    public NettyCodecInitializer(final SslContext sslContext, final NettyInitializer nettyInitializer) throws IllegalArgumentException {
        this.sslContext = sslContext;
        this.nettyInitializer = nettyInitializer;
    }

    @Override
    protected void initChannel(final Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();

        if (this.sslContext != null) pipeline.addLast("ssl", this.sslContext.newHandler(channel.alloc()));
        nettyInitializer.configure(pipeline);
    }
}
