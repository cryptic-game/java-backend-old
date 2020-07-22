package net.cryptic_game.backend.server.server.playground;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.endpoint.ApiParser;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocationProvider;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

@RequiredArgsConstructor
public final class PlaygroundLocationProvider implements HttpLocationProvider<PlaygroundLocation> {

    private final String webSocketAddress;
    private final Collection<ApiEndpointCollectionData> endpointCollections;

    @Override
    public PlaygroundLocation getLocation() {
        return new PlaygroundLocation(new DefaultLastHttpContent(Unpooled.copiedBuffer(
                ApiParser.toPlayground(this.webSocketAddress, this.endpointCollections).toString(), StandardCharsets.UTF_8)));
    }
}
