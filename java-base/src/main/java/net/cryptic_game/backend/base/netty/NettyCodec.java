package net.cryptic_game.backend.base.netty;

import java.util.ArrayList;
import java.util.List;

public abstract class NettyCodec<Initializer extends NettyCodecInitializer<?>> {

    protected final Initializer initializer;
    private final List<NettyCodecInitializer<?>> codecInitializers;

    public NettyCodec(final Initializer initializer, final NettyCodec<?>... childCodecs) {
        this.initializer = initializer;
        this.codecInitializers = new ArrayList<>();
        this.codecInitializers.add(this.initializer);
        for (final NettyCodec<?> child : childCodecs) this.codecInitializers.addAll(child.getInitializers());
    }

    public List<NettyCodecInitializer<?>> getInitializers() {
        return this.codecInitializers;
    }
}
