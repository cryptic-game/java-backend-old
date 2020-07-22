package net.cryptic_game.backend.server.server.playground;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.netty.codec.http.HttpLocation;
import net.cryptic_game.backend.base.utils.HttpUtils;

import java.io.File;
import java.net.URI;

@RequiredArgsConstructor
public final class PlaygroundLocation extends HttpLocation<HttpRequest> {

    private static final String BASE_DIR = new File("www").getAbsolutePath();

    private final LastHttpContent content;

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final HttpRequest msg) throws Exception {
        if (!(msg.method().equals(HttpMethod.GET) || msg.method().equals(HttpMethod.HEAD))) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }

        final URI uri = new URI(msg.uri());
        String path = uri.getPath();
        if (path.endsWith("/")) path += "index.html";
        final File file = new File(BASE_DIR + path).getAbsoluteFile();
        final boolean head = msg.method().equals(HttpMethod.HEAD);

        if (file.getPath().toLowerCase().endsWith(File.separator + "playground.json")) {
            final DefaultHttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
            HttpUtils.setContentTypeHeader(response, "application/json");
            HttpUtils.sendAndCleanupConnection(ctx, msg, response, this.content);
            return;
        }

        if (!HttpUtils.fileExist(BASE_DIR, file)) {
            HttpUtils.sendStatus(ctx, msg, HttpResponseStatus.NOT_FOUND, !head);
            return;
        }

        if (file.isDirectory()) {
            HttpUtils.sendRedirect(ctx, msg, this.getOriginalUri().resolve(this.getOriginalUri().getRawPath() + '/').toASCIIString());
            return;
        }

        HttpUtils.sendFile(ctx, msg, file, head);
    }
}
