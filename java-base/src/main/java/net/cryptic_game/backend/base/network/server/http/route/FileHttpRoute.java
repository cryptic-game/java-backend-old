package net.cryptic_game.backend.base.network.server.http.route;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.utils.HttpUtils;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.File;

public class FileHttpRoute implements HttpRoute {

    private final File baseDir;

    public FileHttpRoute(final File baseDir) {
        this.baseDir = baseDir.getAbsoluteFile();
    }

    @Override
    public Publisher<Void> execute(final HttpServerRequest request, final HttpServerResponse response) {
        final File file = new File(this.baseDir, request.path());

        if (!file.getPath().startsWith(this.baseDir.getPath())
                || !file.exists() || file.isDirectory() || !file.canRead() || !file.canExecute()) {
            return HttpUtils.sendStatus(response, HttpResponseStatus.NOT_FOUND);
        }

        return response.sendFile(file.toPath());
    }
}
