package net.cryptic_game.backend.base.api;

import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.parser.ApiEndpointCollectionParser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class ApiConfiguration {

    public static Set<ApiEndpointCollectionData> filter(final Set<ApiEndpointCollectionData> collections, final ApiType type) {
        return collections.stream()
                .filter(collection -> collection.getType() == type || collection.getType() == ApiType.ALL)
                .collect(Collectors.toSet());
    }

    @Bean
    Set<ApiEndpointCollectionData> collections(
            @ApiEndpointCollection(id = "", type = ApiType.ALL) final Collection<Object> collections,
            final ApplicationContext context
    ) {
        return ApiEndpointCollectionParser.parseCollections(collections, context::getBean);
    }
}
