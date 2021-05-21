package net.cryptic_game.backend.base.network.server.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.time.Duration;

@Slf4j
@Service
public class HttpServerService {

    @Getter
    private final HttpRoutes routes;
    @Getter
    private final HttpServer server;

    public HttpServerService() {
        this.routes = new HttpRoutes();
        this.server = this.createHttpServer("0.0.0.0", 8080);
    }

    private HttpServer createHttpServer(final String host, final int port) {
        return new HttpServer("http", new InetSocketAddress(host, port), Duration.ofSeconds(10), this.routes);
    }

//    private HttpServer createSecureHttpServer(final String host, final int port, final String certPath, final String keyPath) {
//        return new HttpServer("http", new InetSocketAddress(host, port), Duration.ofSeconds(10), this.routes, new File(certPath), new File(keyPath));
//    }

    @PostConstruct
    private void postConstruct() {
        this.server.start();
    }

    @PreDestroy
    private void onDestroy() {
        if (this.server != null) this.server.stop();
    }
}
