package net.cryptic_game.backend.server.server.daemon;

import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClientList;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class DaemonEndpointHandler {

    private static final Logger log = LoggerFactory.getLogger(DaemonEndpointHandler.class);

    private final Set<ApiEndpointCollection> apiCollections;
    private ApiEndpointList apiList;

    public DaemonEndpointHandler() {
        this.apiCollections = new HashSet<>();
        this.apiList = new ApiEndpointList(new ApiClientList());
    }

    public void postInit() {
        try {
            this.apiList.setCollections(this.apiCollections);
        } catch (ApiException e) {
            log.error("Unable to register Api-Collections.", e);
        }
    }

    public <T extends ApiEndpointCollection> T addApiCollection(final T apiCollection) {
        this.apiCollections.add(apiCollection);
        return apiCollection;
    }

    public ApiEndpointList getApiList() {
        return this.apiList;
    }
}
