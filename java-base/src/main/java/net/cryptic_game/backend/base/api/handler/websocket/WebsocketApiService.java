package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.ApiService;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
@ComponentScan
@Service
public class WebsocketApiService {

    private final Map<String, ApiEndpointData> endpoints;
    private final Set<WebsocketApiContext> contexts;

    public WebsocketApiService(
            final ApiService apiService,
            final WebsocketApiConfig config,
            final HttpServerService httpServerService
    ) {
        final Set<ApiEndpointCollectionData> collections = apiService.getCollections(ApiType.WEBSOCKET);
        this.endpoints = ApiEndpointCollectionParser.getEndpoints(collections);
        this.contexts = new LinkedHashSet<>();
        if (!this.endpoints.isEmpty()) {
            httpServerService.getRoutes().addRoute(config.getPath(), new WebsocketApiRoute(this.endpoints, this.contexts));
        }
    }
}
