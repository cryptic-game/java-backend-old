package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.api.client.ApiClientList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class ApiEndpointCollection {

    private static final Logger log = LoggerFactory.getLogger(ApiEndpointCollection.class);
    protected final AppBootstrap appBootstrap;
    private final String name;
    protected ApiClientList clients;

    public ApiEndpointCollection(final String name) {
        this.name = name.strip().toLowerCase();
        this.appBootstrap = AppBootstrap.getInstance();
    }

    Set<ApiEndpointExecutor> load() {
        final Set<ApiEndpointExecutor> executors = new HashSet<>();

        // Load all Methods
        for (final Method method : this.getClass().getDeclaredMethods()) {

            // Filter for API Methods
            if (method.isAnnotationPresent(ApiEndpoint.class)) {
                final String name = method.getAnnotation(ApiEndpoint.class).value();

                // Validate Method
                if (ApiEndpointValidator.validateMethod(this, name, method)) try {

                    // Save Method to API List
                    executors.add(new ApiEndpointExecutor(name, this, method));
                } catch (ApiEndpointParameterException e) {
                    log.error("Error while loading Api endpoint.", e);
                }
            }
        }
        if (executors.size() == 0) log.warn("Api Collection \"" + this.name + "\" has no Apis.");
        return executors;
    }

    void setClients(final ApiClientList clients) {
        this.clients = clients;
    }

    boolean hasClient() {
        return this.clients != null;
    }

    String getName() {
        return this.name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiEndpointCollection)) return false;
        final ApiEndpointCollection that = (ApiEndpointCollection) o;
        return getName().equals(that.getName()) &&
                this.appBootstrap.equals(that.appBootstrap) &&
                Objects.equals(this.clients, that.clients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), this.appBootstrap, this.clients);
    }
}
