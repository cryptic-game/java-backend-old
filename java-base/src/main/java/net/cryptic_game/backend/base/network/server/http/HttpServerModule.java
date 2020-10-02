package net.cryptic_game.backend.base.network.server.http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;

@Slf4j
@ComponentScan
@Configuration
public class HttpServerModule {

    @Getter
    private final HttpRoutes routes;
    @Getter
    private final HttpServer server;

    public HttpServerModule(
            final Bootstrap bootstrap,
            final HttpConfig config,
            final ApiAuthenticationProvider authenticationProvider
    ) {
        this.routes = new HttpRoutes(authenticationProvider);
        if (!config.getCrtPath().isBlank() && !config.getKeyPath().isBlank()) {
            this.server = new HttpServer(
                    "http",
                    new InetSocketAddress(config.getHost(), config.getPort()),
                    Duration.ofSeconds(10),
                    this.routes,
                    new File(config.getCrtPath()),
                    new File(config.getKeyPath())
            );
        } else {
            this.server = new HttpServer(
                    "http",
                    new InetSocketAddress(config.getHost(), config.getPort()),
                    Duration.ofSeconds(10),
                    this.routes
            );
        }
    }

    @PostConstruct
    private void postConstruct() {
        this.server.start();
    }

    @PreDestroy
    private void onDestroy() {
        if (this.server != null) this.server.stop();
    }
}
