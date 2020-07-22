package net.cryptic_game.backend.base.netty.codec.http;

import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.Getter;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;

/**
 * A {@link HttpLocation} is a path on a web space.
 * That meas you can use different handlers for differed paths.
 *
 * @param <T> the type of the message type which is received from the client.
 */
@Getter(AccessLevel.PACKAGE)
public abstract class HttpLocation<T> extends NettyChannelHandler<T> {

    private final ChannelHandler[] parentHandlers;

    /**
     * Creates a new path mapping.
     *
     * @param parentHandlers the parent {@link ChannelHandler}'s which
     *                       are progressed before this {@link HttpLocation}
     *                       is progressed.
     */
    public HttpLocation(final ChannelHandler... parentHandlers) {
        this.parentHandlers = parentHandlers;
    }
}
