package net.cryptic_game.backend.base.netty.codec.http;


import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.utils.HttpUtils;

import java.net.URI;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@EqualsAndHashCode
@Slf4j
class HttpServerContentDecoder extends MessageToMessageDecoder<HttpRequest> {

    private final Map<String, HttpLocationProvider<?>> locationProviders;

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void decode(final ChannelHandlerContext ctx, final HttpRequest msg, final List<Object> out) throws Exception {
        if (HttpUtil.is100ContinueExpected(msg)) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.CONTINUE, false);
        }

        if (!msg.decoderResult().isSuccess()) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.BAD_REQUEST);
            return;
        }

        final URI uri = HttpUtils.decodeUri(msg.uri());
        if (uri == null) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        final String path = uri.getPath();

        final Map.Entry<String, HttpLocation<?>> location = this.getLocation(path.toLowerCase());
        if (location == null) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.NOT_FOUND);
            return;
        } else if (!location.getKey().equals("/") && path.endsWith(location.getKey())) {
            HttpUtils.sendRedirect(ctx, msg, uri.resolve(uri.getRawPath() + '/').toASCIIString());
            return;
        }

        if (!location.getKey().equals("/")) msg.setUri("/" + msg.uri().substring(location.getKey().length() + 1));

        final HttpLocation<?> value = location.getValue();
        value.setOriginalUri(uri);
        this.configurePipeline(ctx.pipeline(), value);
        out.add(msg);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        log.error("Error while processing channel \"" + ctx.channel().toString() + "\".", cause);
    }

    private Map.Entry<String, HttpLocation<?>> getLocation(final String path) {
        for (final Map.Entry<String, HttpLocationProvider<?>> locationProvider : this.locationProviders.entrySet()) {
            if (path.startsWith(locationProvider.getKey())) {
                return new AbstractMap.SimpleEntry<>(locationProvider.getKey(), locationProvider.getValue().getLocation());
            }
        }
        return null;
    }

    private void configurePipeline(final ChannelPipeline pipeline, final HttpLocation<?> location) {
        while (!pipeline.last().getClass().equals(HttpServerContentDecoder.class)) pipeline.removeLast();
        for (final ChannelHandler parentHandler : location.getParentHandlers()) pipeline.addLast(parentHandler);
        pipeline.addLast(location);
    }
}
