package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClientList;
import net.cryptic_game.backend.base.api.notification.NotificationEndpointCollection;

import java.util.HashMap;
import java.util.Set;

public class ApiEndpointList {

    private final HashMap<String, ApiEndpointCollection> collections;
    private final HashMap<String, ApiEndpointExecutor> apiExecutors;

    private final ApiClientList clientList;

    public ApiEndpointList(final ApiClientList clientList) {
        this.collections = new HashMap<>();
        this.apiExecutors = new HashMap<>();
        this.clientList = clientList;
        if (this.clientList != null) {
            final ApiEndpointCollection collection = new NotificationEndpointCollection();
            collection.setClients(this.clientList);
            this.collections.put(collection.getName(), collection);
        }
    }

    private void add(final ApiEndpointExecutor api) {
        this.apiExecutors.put(api.getName().strip().toLowerCase(), api);
    }

    ApiEndpointExecutor get(final String name) {
        return this.apiExecutors.get(name.strip().toLowerCase());
    }

    public void setCollections(final Set<ApiEndpointCollection> apiCollections) throws ApiException {
        for (final ApiEndpointCollection collection : apiCollections) {
            this.collections.put(collection.getName(), collection);
            if (collection.hasClient()) {
                throw new ApiException("Endpoint-Collection \"" + collection.getName() + "\" is already register.");
            }
            collection.setClients(this.clientList);
        }
        this.collections.forEach((name, collection) -> {
            if (!this.apiExecutors.containsKey(name)) collection.load().forEach(this::add);
        });
    }

    public ApiClientList getClientList() {
        return this.clientList;
    }
}
