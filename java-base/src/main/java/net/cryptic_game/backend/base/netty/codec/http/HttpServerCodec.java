package net.cryptic_game.backend.base.netty.codec.http;

import net.cryptic_game.backend.base.netty.codec.NettyCodec;
import net.cryptic_game.backend.base.netty.codec.NettyCodecInitializer;

import java.util.HashMap;
import java.util.Map;

class HttpServerCodec implements NettyCodec {

    private final Map<String, HttpLocationProvider<?>> locationProviders;

    HttpServerCodec() {
        this.locationProviders = new HashMap<>();
    }

    @Override
    public final NettyCodecInitializer getInitializer() {
        return new HttpServerCodecInitializer(this.locationProviders);
    }

    /**
     * Adds a new {@link HttpLocationProvider} to the {@link Map} of location providers.
     *
     * @param path     the path to the {@link HttpLocation}
     * @param provider {@link HttpLocationProvider} instance of the {@link HttpLocation}
     * @see HttpLocationProvider
     * @see HttpLocation
     */
    void addLocationProvider(final String path, final HttpLocationProvider<?> provider) {
        this.locationProviders.put(path.startsWith("/") ? path.toLowerCase() : "/" + path.toLowerCase(), provider);
    }
}
