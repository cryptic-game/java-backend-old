package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import net.cryptic_game.backend.server.server.NettyHandler;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static net.cryptic_game.backend.base.utils.JsonBuilder.simple;

public class HttpServerHandler extends NettyHandler<Object> {

    private final Map<String, HttpEndpoint> endpoints;

    public HttpServerHandler(final Map<String, HttpEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            this.write(ctx, null, this.build(HttpResponseStatus.BAD_REQUEST));
            return;
        }

        final FullHttpRequest request = (FullHttpRequest) msg;
        FullHttpResponse response;

        if (HttpUtil.is100ContinueExpected(request)) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        } else {
            String path = request.uri().toLowerCase();
            if (path.startsWith("/")) path = path.substring(1);

            final HttpEndpoint httpEndpoint = this.endpoints.get(path);

            if (httpEndpoint == null) {
                response = this.build(HttpResponseStatus.NOT_FOUND);
            } else {
                response = this.build(HttpResponseStatus.OK, httpEndpoint.handleRequest());
            }
        }

        this.write(ctx, request, response);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        ctx.write(this.build(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        super.exceptionCaught(ctx, cause);
    }

    private FullHttpResponse build(final HttpResponseStatus status) {
        return this.build(status, simple("error", status.toString()));
    }

    private FullHttpResponse build(final HttpResponseStatus status, final JsonObject json) {
        final byte[] bytes = json.toString().getBytes(StandardCharsets.UTF_8);
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(bytes));

        HttpUtil.setContentLength(response, bytes.length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=" + StandardCharsets.UTF_8);

        return response;
    }

    private void write(final ChannelHandlerContext ctx, final FullHttpRequest request, final FullHttpResponse response) {
        final boolean keepAlive = request != null && HttpUtil.isKeepAlive(request);
        HttpUtil.setKeepAlive(response, keepAlive);

        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.write(response);
        }
    }
}
