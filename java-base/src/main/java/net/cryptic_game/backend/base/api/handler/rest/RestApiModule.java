package net.cryptic_game.backend.base.api.handler.rest;

import lombok.Getter;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import net.cryptic_game.backend.base.network.server.http.HttpServerModule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@ComponentScan
@Configuration
public class RestApiModule {

    @Getter
    private final Set<ApiEndpointCollectionData> collections;

    public RestApiModule(final RestApiConfig config,
                         final HttpServerModule httpServerModule,
                         final ApplicationContext context,
                         final ApiAuthenticationProvider authenticationProvider) {
        final Collection<Object> collections = context.getBeansWithAnnotation(ApiEndpointCollection.class).values();
        this.collections = ApiEndpointCollectionParser.parseCollections(collections, authenticationProvider.getGroups());
        final Map<String, ApiEndpointData> endpoints = ApiEndpointCollectionParser.getEndpoints(this.collections);
        final RestApiLocation handler = new RestApiLocation(endpoints);
        httpServerModule.getRoutes().addRoute(config.getPath(), handler);
    }
}
