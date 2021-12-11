package net.cryptic_game.backend.server;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.DefaultApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.handler.rest.RestApiInitializer;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiInitializer;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiRequest;
import net.cryptic_game.backend.data.sql.repositories.server_management.DisabledEndpointRepository;
import net.cryptic_game.backend.server.daemon.DaemonHandler;
import net.cryptic_game.backend.server.server.websocket.WebSocketDaemonEndpoints;
import de.m4rc3l.nova.core.NovaBanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@Slf4j
@SpringBootApplication(scanBasePackages = "net.cryptic_game.backend")
public class Bootstrap {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .banner(new NovaBanner())
                .run(args);
    }

    @Bean
    DaemonHandler daemonHandler(
            final BaseConfig config,
            final ApplicationContext context,
            final DisabledEndpointRepository disabledEndpointRepository,
            final DefaultApiAuthenticator authenticator
    ) {
        return new DaemonHandler(config.getApiToken(), context, disabledEndpointRepository, authenticator);
    }

    @Bean
    CommandLineRunner runner(
            final WebsocketApiInitializer wsInitializer,
            final RestApiInitializer restInitializer,
            final DisabledEndpointRepository disabledEndpointRepository,
            final DaemonHandler daemonHandler,
            @Value("${LOGIN:false}") boolean login
    ) {
        return args -> {
            final Map<String, ApiEndpointData> endpoints = wsInitializer.getEndpoints();
            daemonHandler.setEndpoints(endpoints);

            // disabling endpoints that are disabled in database
            endpoints.entrySet()
                    .stream()
                    .filter(entry -> disabledEndpointRepository.existsById(entry.getKey()))
                    .forEach(entry -> entry.getValue().setDisabled(false));

            if (!login) {
                restInitializer.getEndpoints()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith("debug"))
                        .forEach(entry -> entry.getValue().setDisabled(false));
            }

            try {
                daemonHandler.setSend(new WebSocketDaemonEndpoints(),
                        WebSocketDaemonEndpoints.class.getDeclaredMethod("send", WebsocketApiRequest.class));
            } catch (NoSuchMethodException e) {
                log.error("Error while setting daemon endpoint handling method.", e);
            }

            System.getenv()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().startsWith("DAEMON_"))
                    .forEach(entry -> daemonHandler.registerDaemon(entry.getKey().substring(7).replace('_', '-').toLowerCase(), entry.getValue()));
        };
    }
}
