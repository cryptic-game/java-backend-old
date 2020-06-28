package net.cryptic_game.backend.base.api.endpoint;

import lombok.Data;
import net.cryptic_game.backend.base.api.client.ApiClient;

import java.util.Set;

@Data
public abstract class ApiEndpointCollection {

    private final String name;
    private final String description;
    private Set<ApiClient> clients;

    final boolean hasClient() {
        return this.clients != null;
    }
}
