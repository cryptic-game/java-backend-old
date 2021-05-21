package net.cryptic_game.backend.base.network.server.http;

import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.network.server.Server;
import org.springframework.util.unit.DataSize;
import reactor.netty.DisposableServer;
import reactor.netty.http.HttpProtocol;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.SocketAddress;
import java.time.Duration;

@Slf4j
@Getter
@RequiredArgsConstructor
public final class HttpServer implements Server {

    private final String id;
    private final SocketAddress address;
    private final Duration lifecycleTimeout;
    private final HttpRoutes routes;

    private File certificateFile;
    private File keyFile;

    private DisposableServer server;

    public HttpServer(final String id, final SocketAddress address, final Duration lifecycleTimeout, final HttpRoutes routes, final File certificateFile, final File keyFile) {
        this.id = id;
        this.address = address;
        this.lifecycleTimeout = lifecycleTimeout;
        this.routes = routes;
        this.certificateFile = certificateFile;
        this.keyFile = keyFile;
    }

    @Override
    public void start() {
        reactor.netty.http.server.HttpServer server = reactor.netty.http.server.HttpServer.create()
                .bindAddress(() -> this.address)
                .compress((int) DataSize.ofKilobytes(5).toBytes())
                .forwarded(true);

        if (this.certificateFile == null || this.keyFile == null)
            server = server.protocol(HttpProtocol.HTTP11, HttpProtocol.H2C);
        else {
            log.info("Using certificate ({}) and private key ({}) for http server {} with SSL.",
                    this.certificateFile.getAbsolutePath(), this.keyFile.getAbsolutePath(), this.id);
            if (OpenSsl.isAvailable()) log.info("Using native SSL implementation.");
            server = server.protocol(HttpProtocol.HTTP11, HttpProtocol.H2)
                    .secure(spec -> {
                        try {
                            spec.sslContext(SslContextBuilder.forServer(this.certificateFile, this.keyFile).build());
                        } catch (SSLException e) {
                            log.error("Error while building ssl context", e);
                        }
                    });
        }

        server = server.handle(this.routes.toHandler());

        try {
            this.server = server.bindNow(this.lifecycleTimeout);

            log.info("Http Server {} is now listening on {}.", this.id, this.getAddress());

            this.server.onDispose()
                    .doFinally(signalType -> this.server = null)
                    .subscribe();

            new Thread(this.server.onDispose()::block, "server-" + this.id).start();
        } catch (Throwable cause) {
            log.error("Unable to start http server {}: {}", this.id, cause.getMessage());

            throw cause;
        }
    }

    @Override
    public void stop() {
        if (!this.isRunning())
            throw new IllegalStateException("Error while stopping server " + this.id + ", it is not running.");

        this.server.disposeNow(this.lifecycleTimeout);
    }

    @Override
    public boolean isRunning() {
        return this.server != null;
    }

    public SocketAddress getAddress() {
        return this.isRunning() ? this.server.address() : this.address;
    }
}
