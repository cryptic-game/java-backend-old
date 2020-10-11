package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ComponentScan
@Configuration
public class WebsocketApiModule {

    public WebsocketApiModule(final ApiModule apiModule,
                              final WebsocketApiConfig config,
                              final HttpServerModule httpServerModule) {
        Set<ApiEndpointCollectionData> collections = apiModule.getCollections()
                .parallelStream()
                .filter(apiEndpointCollectionData -> apiEndpointCollectionData.getApiType().equals(ApiType.WEBSOCKET)
                        || apiEndpointCollectionData.getApiType().equals(ApiType.ALL)).collect(Collectors.toSet());
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(collections);
        if (collections.isEmpty()) return;
        httpServerModule.getRoutes().addRoute(config.getPath(), new WebsocketApiLocation(endpoints));
    }
}
