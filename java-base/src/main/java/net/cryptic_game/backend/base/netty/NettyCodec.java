package net.cryptic_game.backend.base.netty;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
public abstract class NettyCodec<I extends NettyCodecInitializer<?>> {

    private final I initializer;
    @Getter
    private final List<NettyCodecInitializer<?>> initializers;

    public NettyCodec(final I initializer, final NettyCodec<?>... childCodecs) {
        this.initializer = initializer;
        this.initializers = new ArrayList<>();
        this.initializers.add(this.initializer);
        if (childCodecs.length > 0) this.setChildCodecs(childCodecs);
    }

    public final void setChildCodecs(final NettyCodec<?>... childCodecs) {
        for (final NettyCodec<?> child : childCodecs) this.initializers.addAll(child.initializers);
    }
}
