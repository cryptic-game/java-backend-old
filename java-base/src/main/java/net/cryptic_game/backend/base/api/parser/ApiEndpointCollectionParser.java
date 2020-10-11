package net.cryptic_game.backend.base.api.parser;

import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ApiEndpointCollectionParser {

    private ApiEndpointCollectionParser() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Set<ApiEndpointCollectionData> parseCollections(@NotNull final Collection<Object> instances, @NotNull final Set<Group> groups) {
        return instances.stream()
                .map((Object instance) -> parseCollection(instance, groups))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @NotNull
    public static Map<String, ApiEndpointData> getEndpoints(@NotNull final Set<ApiEndpointCollectionData> collections) {
        final Map<String, ApiEndpointData> endpoints = new HashMap<>();
        collections.forEach(collection -> collection.getEndpoints().forEach((key, value) -> endpoints.put(collection.getId() + "/" + key, value)));
        return endpoints;
    }

    @Nullable
    public static ApiEndpointCollectionData parseCollection(@NotNull final Object instance, @NotNull final Set<Group> groups) {
        final Class<?> clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(ApiEndpointCollection.class)) return null;

        final ApiEndpointCollection endpointCollectionAnnotation = clazz.getAnnotation(ApiEndpointCollection.class);
        return new ApiEndpointCollectionData(
                endpointCollectionAnnotation.id(),
                String.join("\n", endpointCollectionAnnotation.description()),
                endpointCollectionAnnotation.apiType(),
                ApiEndpointParser.parseEndpoints(instance, clazz, groups, endpointCollectionAnnotation.disabled())
        );
    }
}
