package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.api.client.ApiClient;

import java.util.Objects;
import java.util.Set;

public abstract class ApiEndpointCollection {

    private final String name;
    private final String description;
    protected Set<ApiClient> clients;

    public ApiEndpointCollection(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    final boolean hasClient() {
        return this.clients != null;
    }

    public final String getName() {
        return this.name;
    }


    public final String getDescription() {
        return this.description;
    }

    final void setClients(final Set<ApiClient> clients) {
        this.clients = clients;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiEndpointCollection)) return false;
        ApiEndpointCollection that = (ApiEndpointCollection) o;
        return getName().equals(that.getName())
                && getDescription().equals(that.getDescription())
                && Objects.equals(clients, that.clients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), clients);
    }
}
