package net.cryptic_game.backend.base.netty;

import io.netty.channel.ChannelPipeline;
import lombok.Data;

@Data
public abstract class NettyCodecInitializer<C extends NettyCodec<?>> {

    private C codec;

    public abstract void configure(ChannelPipeline pipeline);
}
