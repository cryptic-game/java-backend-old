package net.cryptic_game.backend.server.server.websocket.playground;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public final class PlaygroundUtils {

    public static final DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter.RFC_1123_DATE_TIME;
    public static final ZoneOffset HTTP_TIME_ZONE = ZoneOffset.UTC;
    private static final String INDEX_FILE = "index.html";
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private static final int HTTP_CACHE_SECONDS = 60;
    private static final MimetypesFileTypeMap MIMETYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();

    static {
        MIMETYPES_FILE_TYPE_MAP.addMimeTypes("application/javascript js");
        MIMETYPES_FILE_TYPE_MAP.addMimeTypes("text/css css");
    }

    private PlaygroundUtils() {
        throw new UnsupportedOperationException();
    }

    public static String convertUriToPath(final String uri) {
        final String decodedUri = URLDecoder.decode(uri, StandardCharsets.UTF_8);

        if (uri.isEmpty()
                || uri.charAt(0) != '/'
                || decodedUri.contains("/.")
                || decodedUri.contains("./")
                || uri.charAt(0) == '.'
                || uri.charAt(uri.length() - 1) == '.'
                || INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        final String path = decodedUri.replace('/', File.separatorChar);
        return path.endsWith(File.separator) ? path + INDEX_FILE : path;
    }

    public static void setDateHeader(FullHttpResponse response) {
        response.headers().set(HttpHeaderNames.DATE, HTTP_DATE_FORMAT.format(ZonedDateTime.now(ZoneOffset.UTC)));
    }

    public static void setDateAndCacheHeaders(final HttpResponse response, final File fileToCache) {
        final ZonedDateTime time = ZonedDateTime.now(ZoneOffset.UTC);
        response.headers().set(HttpHeaderNames.DATE, HTTP_DATE_FORMAT.format(time));
        response.headers().set(HttpHeaderNames.EXPIRES, HTTP_DATE_FORMAT.format(time.plus(HTTP_CACHE_SECONDS, ChronoUnit.SECONDS)));
        response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + HTTP_CACHE_SECONDS);
        response.headers().set(HttpHeaderNames.LAST_MODIFIED, HTTP_DATE_FORMAT.format(ZonedDateTime.ofInstant(Instant.ofEpochMilli(fileToCache.lastModified()), HTTP_TIME_ZONE)));
    }

    public static void setContentTypeHeader(final HttpResponse response, final File file) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, MIMETYPES_FILE_TYPE_MAP.getContentType(file));
    }

    public static boolean checkModified(final Instant instant, final File file) {
        return !Instant.ofEpochMilli(file.lastModified()).equals(instant);
    }
}
