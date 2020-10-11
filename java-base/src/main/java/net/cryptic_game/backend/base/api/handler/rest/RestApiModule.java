package net.cryptic_game.backend.base.api.handler.rest;

import lombok.Getter;
import net.cryptic_game.backend.base.api.ApiModule;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerModule;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ComponentScan
@Configuration
public class RestApiModule {

    @Getter
    private final Set<ApiEndpointCollectionData> collections;

    public RestApiModule(final ApiModule apiModule,
                         final RestApiConfig config,
                         final HttpServerModule httpServerModule) {
        this.collections = apiModule.getCollections()
                .parallelStream()
                .filter(apiEndpointCollectionData -> apiEndpointCollectionData.getApiType().equals(ApiType.REST)
                        || apiEndpointCollectionData.getApiType().equals(ApiType.ALL)).collect(Collectors.toSet());
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(this.collections);
        if (endpoints.isEmpty()) return;
        httpServerModule.getRoutes().addRoute(config.getPath(), new RestApiLocation(endpoints));
    }
}
