package net.cryptic_game.backend.base.netty.codec.http;

import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;

import java.net.URI;

/**
 * A {@link HttpLocation} is a path on a web space.
 * That meas you can use different handlers for differed paths.
 *
 * @param <T> the type of the message type which is received from the client.
 */

@Setter
public abstract class HttpLocation<T> extends NettyChannelHandler<T> {

    @Getter(AccessLevel.PACKAGE)
    private final ChannelHandler[] parentHandlers;

    @Getter(AccessLevel.PROTECTED)
    private URI originalUri;

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
