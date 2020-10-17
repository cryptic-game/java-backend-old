package net.cryptic_game.backend.base.api.handler.rest;

import lombok.Getter;
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

@Getter
@Service
@ComponentScan
public class RestApiService {

    private final Set<ApiEndpointCollectionData> collections;

    public RestApiService(
            final ApiService apiService,
            final RestApiConfig config,
            final HttpServerService httpServerService
    ) {
        this.collections = apiService.getCollections(ApiType.REST);
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(collections);
        if (!endpoints.isEmpty()) {
            httpServerService.getRoutes().addRoute(config.getPath(), new RestApiRoute(endpoints));
        }
    }
}
