package net.cryptic_game.backend.base.netty;

import io.netty.channel.ChannelPipeline;

public abstract class NettyCodecInitializer<Codec extends NettyCodec<?>> {

    private Codec codec;

    public abstract void configure(final ChannelPipeline pipeline);

    protected Codec getCodec() {
        return this.codec;
    }

    public void setCodec(final Codec codec) {
        this.codec = codec;
    }
}
