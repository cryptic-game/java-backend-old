package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ApiEndpointList {

    private final Map<String, ApiEndpointCollectionData> collections;
    private final Map<String, ApiEndpointData> endpoints;
    private final Set<ApiClient> clients;

    public ApiEndpointList() {
        this.collections = new HashMap<>();
        this.endpoints = new HashMap<>();
        this.clients = new HashSet<>();
    }

    public Map<String, ApiEndpointCollectionData> getCollections() {
        return this.collections;
    }

    public void setCollections(final Set<ApiEndpointCollection> apiCollections) throws ApiException {
        for (final ApiEndpointCollection collection : apiCollections) {
            this.collections.put(collection.getName(), ApiParser.parseEndpointCollection(collection));
            if (collection.hasClient()) {
                throw new ApiException("Endpoint-Collection \"" + collection.getName() + "\" is already register.");
            }
            collection.setClients(this.clients);
        }
        this.collections.forEach((name, collectionData) -> collectionData.getEndpoints()
                .forEach((endpointName, endpointData) -> this.endpoints.merge(endpointName, endpointData, (mergeName, mergeEndpointData) -> mergeEndpointData)));
    }

    public void addCollections(final Collection<ApiEndpointCollectionData> endpointCollections) {
        endpointCollections.forEach(collection -> {
            this.collections.put(collection.getName(), collection);
            collection.getEndpoints().forEach((endpointName, endpointData) -> this.endpoints
                    .merge(endpointName, endpointData, (mergeName, mergeEndpointData) -> mergeEndpointData));
        });
    }

    public void remove(final ApiEndpointCollectionData endpointCollection) {
        this.collections.remove(endpointCollection.getName());
        endpointCollection.getEndpoints().forEach((name, endpoint) -> this.endpoints.remove(name));
    }

    public Map<String, ApiEndpointData> getEndpoints() {
        return this.endpoints;
    }

    public Set<ApiClient> getClients() {
        return this.clients;
    }
}
