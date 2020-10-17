package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.ApiService;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Slf4j
@ComponentScan
@Service
public class WebsocketApiService {

    public WebsocketApiService(
            final ApiService apiService,
            final WebsocketApiConfig config,
            final HttpServerService httpServerService
    ) {
        final Set<ApiEndpointCollectionData> collections = apiService.getCollections(ApiType.WEBSOCKET);
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(collections);
        if (!endpoints.isEmpty()) {
            httpServerService.getRoutes().addRoute(config.getPath(), new WebsocketApiRoute(endpoints));
        }
    }
}
