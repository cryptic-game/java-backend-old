package net.cryptic_game.backend.base.api;

import lombok.Getter;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class ApiModule {

    @Getter
    private final Set<ApiEndpointCollectionData> collections;

    public ApiModule(final Bootstrap bootstrap,
                     final ApiAuthenticationProvider authenticationProvider) {
        final Collection<Object> collections = bootstrap.getContextHandler().getBeansWithAnnotation(ApiEndpointCollection.class).values();
        this.collections = ApiEndpointCollectionParser.parseCollections(collections, authenticationProvider.getGroups());
    }
}
