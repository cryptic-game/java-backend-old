package net.cryptic_game.backend.server.api.handler.websocket;

import lombok.Getter;
import net.cryptic_game.backend.base.api.ApiConfiguration;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerService;
import net.cryptic_game.backend.server.redis.OnlineUsersCounter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebsocketApiInitializer implements CommandLineRunner {

    @Getter
    private final Set<WebsocketApiContext> contexts;
    private final HttpServerService serverService;
    private final Set<ApiEndpointCollectionData> collections;
    private final OnlineUsersCounter counter;

    @Getter
    private Map<String, ApiEndpointData> endpoints;

    public WebsocketApiInitializer(final HttpServerService serverService, @Lazy final Set<ApiEndpointCollectionData> collections, final OnlineUsersCounter counter) {
        this.serverService = serverService;
        this.collections = collections;
        this.counter = counter;
        this.contexts = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    @Override
    public void run(final String[] args) throws Exception {
        this.endpoints = ApiEndpointCollectionParser.getEndpoints(ApiConfiguration.filter(this.collections, ApiType.WEBSOCKET));
        if (endpoints.isEmpty()) {
            return;
        }

        this.serverService.getRoutes().addRoute("ws", new WebsocketApiRoute(this.endpoints, this.contexts, counter));
    }
}
