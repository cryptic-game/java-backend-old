package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerModule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

@Slf4j
@ComponentScan
@Configuration
public class WebsocketApiModule {

    public WebsocketApiModule(final WebsocketApiConfig config,
                              final HttpServerModule httpServerModule,
                              final ApplicationContext context,
                              final ApiAuthenticationProvider authenticationProvider) {
        final Collection<Object> collections = context.getBeansWithAnnotation(ApiEndpointCollection.class).values();
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(ApiEndpointCollectionParser
                .parseCollections(collections, authenticationProvider.getGroups()));
        httpServerModule.getRoutes().addRoute(config.getPath(), new WebsocketApiLocation(endpoints));
    }
}
