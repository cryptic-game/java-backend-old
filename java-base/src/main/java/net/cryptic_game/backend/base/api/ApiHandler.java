package net.cryptic_game.backend.base.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiHandler {

    private final Map<String, ApiEndpointExecutor> executors;
    private final Class<? extends ApiEndpointExecutor> executorClass;

    public ApiHandler(Class<? extends ApiEndpointExecutor> executorClass) {
        this.executors = new HashMap<>();
        this.executorClass = executorClass;
    }

    public ApiEndpointExecutor getEndpointExecutor(String endpoint) {
        return executors.get(endpoint);
    }

    public void registerEndpoint(final ApiEndpointExecutor executor) {
        this.executors.put(executor.getName(), executor);
    }

    public void registerApiCollection(final ApiCollection collection) {
        collection.load(executorClass).forEach(this::registerEndpoint);
    }

    public void registerApiCollections(final List<ApiCollection> apiCollections) {
        apiCollections.forEach(this::registerApiCollection);
    }
}
