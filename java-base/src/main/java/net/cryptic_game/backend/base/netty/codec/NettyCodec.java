package net.cryptic_game.backend.base.netty.codec;

public interface NettyCodec {

    /**
     * Returns the {@link NettyCodecInitializer} related to the current {@link NettyCodec}.
     *
     * @return the {@link NettyCodecInitializer} which is related to the current {@link NettyCodec}
     */
    NettyCodecInitializer getInitializer();
}
