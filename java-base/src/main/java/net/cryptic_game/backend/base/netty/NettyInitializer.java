package net.cryptic_game.backend.base.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslContext;

import java.util.List;

public class NettyInitializer extends ChannelInitializer<Channel> {

    private final SslContext sslContext;
    private final List<NettyCodecInitializer<?>> nettyCodecInitializers;

    public NettyInitializer(final SslContext sslContext, final List<NettyCodecInitializer<?>> nettyCodecInitializers) {
        this.sslContext = sslContext;
        this.nettyCodecInitializers = nettyCodecInitializers;
    }

    @Override
    protected void initChannel(final Channel channel) {
        final ChannelPipeline pipeline = channel.pipeline();

        if (this.sslContext != null) pipeline.addLast("ssl", this.sslContext.newHandler(channel.alloc()));
        this.nettyCodecInitializers.forEach(initializer -> initializer.configure(pipeline));
    }
}
