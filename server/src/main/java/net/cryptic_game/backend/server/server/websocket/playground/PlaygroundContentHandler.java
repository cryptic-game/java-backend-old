package net.cryptic_game.backend.server.server.websocket.playground;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import net.cryptic_game.backend.base.netty.NettyChannelHandler;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

public class PlaygroundContentHandler extends NettyChannelHandler<FullHttpRequest> {

    private static final String BASE_DIR = "www";
    private static final String LOCATION = File.separatorChar + "playground";
    private static final String QUOTED_LOCATION = Pattern.quote(LOCATION);

    private FullHttpRequest request;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final FullHttpRequest msg) throws Exception {
        this.request = msg;

        final String path = PlaygroundUtils.convertUriToPath(msg.uri());
        if (path == null || !path.startsWith(LOCATION)) {
            this.sendStatus(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }

        final File file = new File(BASE_DIR + path.replaceFirst(Pattern.compile(QUOTED_LOCATION).pattern(), ""));
        if (checkFile(ctx, msg.uri(), file)) this.sendFile(ctx, file);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        this.sendStatus(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendFile(final ChannelHandlerContext ctx, final File file) throws IOException {
        final String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null
                && !ifModifiedSince.isEmpty()
                && PlaygroundUtils.checkModified(ZonedDateTime.parse(ifModifiedSince, PlaygroundUtils.HTTP_DATE_FORMAT).toInstant(), file)) {
            this.sendNotModified(ctx);
            return;
        }

        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            final long length = randomAccessFile.length();
            final boolean keepAlive = HttpUtil.isKeepAlive(this.request);

            final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpUtil.setContentLength(response, length);
            PlaygroundUtils.setContentTypeHeader(response, file);
            PlaygroundUtils.setDateAndCacheHeaders(response, file);
            HttpUtil.setKeepAlive(response, keepAlive);
            ctx.write(response);

            ChannelFuture lastContentFuture;
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0, length), ctx.newProgressivePromise());
                lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } else {
                lastContentFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(randomAccessFile, 0, length, 8192)), ctx.newProgressivePromise());
            }

            if (!keepAlive) lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean checkFile(final ChannelHandlerContext ctx, final String uri, final File file) {
        if ((file.isHidden() || !file.exists()) && !uri.endsWith("/")) {
            this.sendStatus(ctx, HttpResponseStatus.NOT_FOUND);
            return false;
        }

        if (file.isDirectory()) {
            if (!uri.endsWith("/")) this.sendRedirect(ctx, uri + "/");
            else this.sendStatus(ctx, HttpResponseStatus.FORBIDDEN);
            return false;
        }

        if (!file.isFile()) {
            this.sendStatus(ctx, HttpResponseStatus.FORBIDDEN);
            return false;
        }
        return true;
    }

    private void sendRedirect(final ChannelHandlerContext ctx, final String newUri) {
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND, Unpooled.EMPTY_BUFFER);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        this.sendAndCleanupConnection(ctx, response);
    }

    private void sendNotModified(final ChannelHandlerContext ctx) {
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        PlaygroundUtils.setDateHeader(response);
        this.sendAndCleanupConnection(ctx, response);
    }

    private void sendStatus(final ChannelHandlerContext ctx, final HttpResponseStatus status) {
        final FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=" + StandardCharsets.UTF_8.toString());
        this.sendAndCleanupConnection(ctx, response);
    }

    private void sendAndCleanupConnection(final ChannelHandlerContext ctx, final FullHttpResponse response) {
        final boolean keepAlive = HttpUtil.isKeepAlive(this.request);

        HttpUtil.setContentLength(response, response.content().readableBytes());
        HttpUtil.setKeepAlive(response, keepAlive);

        if (!keepAlive) ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        else ctx.writeAndFlush(response);
    }
}
