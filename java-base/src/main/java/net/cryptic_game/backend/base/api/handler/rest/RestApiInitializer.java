package net.cryptic_game.backend.base.api.handler.rest;

import lombok.Getter;
import net.cryptic_game.backend.base.api.ApiConfiguration;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class RestApiInitializer implements CommandLineRunner {

    private final HttpServerService serverService;
    @Getter
    private final Set<ApiEndpointCollectionData> collections;

    public RestApiInitializer(final HttpServerService serverService, @Lazy final Set<ApiEndpointCollectionData> collections) {
        this.serverService = serverService;
        this.collections = collections;
    }

    @Override
    public void run(final String... args) {
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(ApiConfiguration.filter(this.collections, ApiType.REST));

        if (endpoints.isEmpty()) {
            return;
        }

        this.serverService.getRoutes().addRoute("api", new RestApiRoute(endpoints));
    }
}
