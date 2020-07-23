package net.cryptic_game.backend.base.netty.codec;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
@EqualsAndHashCode
public final class NettyCodecHandler {

    private final List<NettyCodecInitializer> initializers;

    /**
     * Adds some codecs to this {@link NettyCodecHandler} witch
     * is able to provide all {@link NettyCodecInitializer} in
     * the right order for a server.
     *
     * @param codecs the codecs which should be configured
     */
    public NettyCodecHandler(final NettyCodec... codecs) {
        this.initializers = new LinkedList<>();
        for (final NettyCodec codec : codecs) this.initializers.add(codec.getInitializer());
    }
}
