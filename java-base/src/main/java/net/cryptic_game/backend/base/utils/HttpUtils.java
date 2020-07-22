package net.cryptic_game.backend.base.utils;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

@Slf4j
public final class HttpUtils {

    public static final DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
    public static final ZoneOffset HTTP_TIME_ZONE = ZoneOffset.UTC;
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static final long HTTP_CACHE_SECONDS = Duration.ofDays(30).toSeconds();
    private static final MimetypesFileTypeMap MIMETYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();

    static {
        MIMETYPES_FILE_TYPE_MAP.addMimeTypes("application/javascript js");
        MIMETYPES_FILE_TYPE_MAP.addMimeTypes("text/css css");
    }

    private HttpUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validated and decodes a uri.
     *
     * @param uri the uri form the client
     * @return the decoded uri
     */
    public static URI decodeUri(final String uri) {
        final String decoded = URLDecoder.decode(uri, StandardCharsets.UTF_8);

        if (uri.isBlank()
                || uri.charAt(0) != '/'
                || uri.charAt(0) == '.'
                || uri.charAt(uri.length() - 1) == '.'
                || uri.contains("..")
                || uri.contains("./")
                || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        return URI.create(uri);
    }

    /**
     * Formats milliseconds to a Http Date. ({@link DateTimeFormatter#RFC_1123_DATE_TIME})
     *
     * @param milliseconds the time witch should be formatted
     * @return the formatted date
     */
    public static String formatDate(final long milliseconds) {
        return formatDate(Instant.ofEpochMilli(milliseconds).atOffset(HTTP_TIME_ZONE));
    }

    /**
     * Formats a {@link OffsetDateTime} to a Http Date. ({@link DateTimeFormatter#RFC_1123_DATE_TIME})
     *
     * @param dateTime the {@link OffsetDateTime} witch should be formatted
     * @return the formatted date
     */
    public static String formatDate(final OffsetDateTime dateTime) {
        return dateTime.format(HTTP_DATE_FORMAT);
    }

    /**
     * Checks if a file is accessible or exists.
     *
     * @param baseDir the Directory were the file shuld be located (This <b>MUST</b> be an absolute path!)
     * @param file    the {@link File} witch should be checked
     * @return if the file is accessible or exists
     */
    public static boolean fileExist(final String baseDir, final File file) {
        return file.exists() && file.canRead() && !file.isHidden() && file.getPath().startsWith(baseDir);
    }

    /**
     * Writes a {@link File} to a {@link ChannelHandlerContext}.
     *
     * @param ctx      the {@link ChannelHandlerContext} to which the message should be send
     * @param request  the request from the {@link ChannelHandlerContext} (Client)
     * @param file     the {@link File} which should be send to the {@link ChannelHandlerContext} (Client)
     * @param onlyHead only send the {@link HttpResponse} without the {@link HttpContent}. This is needed at {@link io.netty.handler.codec.http.HttpMethod#HEAD}
     * @throws IOException if there is an error while reading the file
     */
    public static void sendFile(final ChannelHandlerContext ctx, final HttpRequest request, final File file, final boolean onlyHead) throws IOException {
        final String ifModifiedSince = request.headers().get(HttpHeaderNames.IF_MODIFIED_SINCE);
        if (ifModifiedSince != null
                && !ifModifiedSince.isEmpty()
                && checkModified(OffsetDateTime.parse(ifModifiedSince, HTTP_DATE_FORMAT).toInstant(), file)) {
            sendNotModified(ctx, request);
            return;
        }

        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        final long length = randomAccessFile.length();
        final boolean keepAlive = HttpUtil.isKeepAlive(request);

        final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, length);
        setContentTypeHeader(response, file);
        setDateAndCacheHeaders(response, file);
        HttpUtil.setKeepAlive(response, keepAlive);

        ChannelFuture lastContentFuture = ctx.write(response);

        if (!onlyHead) if (ctx.pipeline().get(SslHandler.class) == null) {
            ctx.write(new DefaultFileRegion(randomAccessFile.getChannel(), 0, length), ctx.newProgressivePromise());
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } else {
            lastContentFuture = ctx.writeAndFlush(new HttpChunkedInput(new ChunkedFile(randomAccessFile, 0, length, 8192)), ctx.newProgressivePromise());
        }

        if (!keepAlive) lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * Adds the HTTP {@code Date} header to a {@link HttpResponse}.
     * The date is formatted in the {@link DateTimeFormatter#RFC_1123_DATE_TIME} format.
     *
     * @param response the {@link HttpResponse} to which the date header should be added
     */
    public static void setDateHeader(final HttpResponse response) {
        response.headers().set(HttpHeaderNames.DATE, formatDate(OffsetDateTime.now(HTTP_TIME_ZONE)));
    }

    /**
     * Adds the HTTP {@code Date} and all required Cache headers ({@code Expires},
     * {@code Cache-Control}, {@code Last-Modified}) to a {@link HttpResponse}.
     * The date is formatted in the {@link DateTimeFormatter#RFC_1123_DATE_TIME} format.
     *
     * @param response    the {@link HttpResponse} to which the date and cache headers should be added
     * @param fileToCache the {@link File} from from which the cache data should be extracted
     */
    public static void setDateAndCacheHeaders(final HttpResponse response, final File fileToCache) {
        final OffsetDateTime time = OffsetDateTime.now(HTTP_TIME_ZONE);
        response.headers().set(HttpHeaderNames.DATE, formatDate(time));
        response.headers().set(HttpHeaderNames.EXPIRES, formatDate(time.plus(HTTP_CACHE_SECONDS, ChronoUnit.SECONDS)));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, formatDate(fileToCache.lastModified()));
    }

    /**
     * Adds the HTTP {@code Content-Type} header to a {@link HttpMessage}.
     *
     * @param message the {@link HttpMessage} to which the content type header should be added
     * @param file    the file from were the content type should be extracted
     */
    public static void setContentTypeHeader(final HttpMessage message, final File file) {
        setContentTypeHeader(message, MIMETYPES_FILE_TYPE_MAP.getContentType(file));
    }

    /**
     * Adds the HTTP {@code Content-Type} header to a {@link HttpMessage}.
     *
     * @param message     the {@link HttpMessage} to which the content type header should be added
     * @param contentType the content type as a {@link CharSequence} which should be used
     */
    public static void setContentTypeHeader(final HttpMessage message, final CharSequence contentType) {
        message.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    /**
     * Adds the HTTP {@code Content-Type} header to a {@link HttpMessage}.
     *
     * @param message     the {@link HttpMessage} to which the content type header should be added
     * @param contentType the content type as a {@link CharSequence} which should be used
     * @param charset     the {@link Charset} of the content type
     */
    public static void setContentTypeHeader(final HttpMessage message, final CharSequence contentType, final Charset charset) {
        message.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=" + charset.toString());
    }

    /**
     * Checks if the {@link File} has been edited since the specified {@link Instant}.
     *
     * @param instant the {@link Instant} with which it should be checked if the {@link File} has been edited
     * @param file    the {@link File} which should be checked
     * @return if the {@link File} has been edited since the specified {@link Instant}
     */
    public static boolean checkModified(final Instant instant, final File file) {
        return instant.getEpochSecond() != file.lastModified() / 1000;
    }

    /**
     * Sends a redirect ot the new uri to the specified {@link ChannelHandlerContext}.
     *
     * @param ctx     the {@link ChannelHandlerContext} to which the redirect should be send
     * @param request the {@link HttpRequest} from the {@link ChannelHandlerContext}
     * @param newUri  the new uri to witch the client should be redirected
     */
    public static void sendRedirect(final ChannelHandlerContext ctx, final HttpRequest request, final String newUri) {
        final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        sendAndCleanupConnection(ctx, request, response, null);
    }

    /**
     * Sends to the {@link ChannelHandlerContext} thad the requested content is not modified science the last.
     *
     * @param ctx     the {@link ChannelHandlerContext} to which the redirect should be send
     * @param request the {@link HttpRequest} from the {@link ChannelHandlerContext}
     */
    public static void sendNotModified(final ChannelHandlerContext ctx, final HttpRequest request) {
        final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED);
        setDateHeader(response);
        sendAndCleanupConnection(ctx, request, response, null);
    }

    /**
     * Send a status to a {@link ChannelHandlerContext} (Client).
     *
     * @param ctx     the {@link ChannelHandlerContext} to which the message should be send
     * @param request the request from the {@link ChannelHandlerContext} (Client)
     * @param status  the status which should be send to the {@link ChannelHandlerContext} (Client)
     */
    public static void sendStatus(final ChannelHandlerContext ctx, final HttpRequest request, final HttpResponseStatus status) {
        sendStatus(ctx, request, status, true);
    }

    /**
     * Send a status to a {@link ChannelHandlerContext} (Client).
     *
     * @param ctx     the {@link ChannelHandlerContext} to which the message should be send
     * @param request the request from the {@link ChannelHandlerContext} (Client)
     * @param status  the status which should be send to the {@link ChannelHandlerContext} (Client)
     * @param body    if the status code should be send also via the {@link HttpContent} (Http Body)
     */
    public static void sendStatus(final ChannelHandlerContext ctx, final HttpRequest request, final HttpResponseStatus status, final boolean body) {
        final HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        if (body) setContentTypeHeader(request, "text/plain; charset=" + StandardCharsets.UTF_8.toString());
        sendAndCleanupConnection(ctx, request, response, body ? new DefaultHttpContent(Unpooled.copiedBuffer(status.toString() + "\r\n", CharsetUtil.UTF_8)) : null);
    }

    /**
     * Sends a response to a {@link ChannelHandlerContext} (Client).
     *
     * @param ctx      the {@link ChannelHandlerContext} to which the message should be send
     * @param request  the request from the {@link ChannelHandlerContext} (Client)
     * @param response the response which should be send to the {@link ChannelHandlerContext} (Client)
     * @param content  the content which should be send to the {@link ChannelHandlerContext} (Client)
     */
    public static void sendAndCleanupConnection(final ChannelHandlerContext ctx, final HttpRequest request, final HttpResponse response, final HttpContent content) {
        final boolean keepAlive = HttpUtil.isKeepAlive(request);

        HttpUtil.setContentLength(response, content == null ? 0 : content.content().readableBytes());
        HttpUtil.setKeepAlive(response, keepAlive);

        final ChannelFuture channelFuture;
        if (content == null) channelFuture = ctx.write(response);
        else {
            ctx.write(response);
            channelFuture = ctx.write(content);
        }
        if (!keepAlive) channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
}
