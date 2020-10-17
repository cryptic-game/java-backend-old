package net.cryptic_game.backend.base.api.parser;

import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.AuthenticatorSupplier;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ApiEndpointCollectionParser {

    private ApiEndpointCollectionParser() {
        throw new UnsupportedOperationException();
    }

    public static Set<ApiEndpointCollectionData> parseCollections(final Collection<Object> instances, final AuthenticatorSupplier authenticatorSupplier) {
        return instances.stream()
                .map(instance -> parseCollection(instance, authenticatorSupplier))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Map<String, ApiEndpointData> getEndpoints(final Set<ApiEndpointCollectionData> collections) {
        return collections.parallelStream()
                .flatMap(collection -> collection.getEndpoints().entrySet().parallelStream()
                        .map(entry -> new AbstractMap.SimpleEntry<>(collection.getId() + "/" + entry.getKey(), entry.getValue())))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static ApiEndpointCollectionData parseCollection(final Object instance, final AuthenticatorSupplier authenticatorSupplier) {
        final Class<?> clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(ApiEndpointCollection.class)) return null;

        final ApiEndpointCollection endpointCollectionAnnotation = clazz.getAnnotation(ApiEndpointCollection.class);
        final Map<String, ApiEndpointData> parameters = ApiEndpointParser.parseEndpoints(instance, clazz,
                endpointCollectionAnnotation.disabled(), authenticatorSupplier.apply(endpointCollectionAnnotation.authenticator()));

        return new ApiEndpointCollectionData(
                endpointCollectionAnnotation.id(),
                String.join("\n", endpointCollectionAnnotation.description()),
                endpointCollectionAnnotation.type(),
                endpointCollectionAnnotation.disabled(),
                parameters
        );
    }
}
