package net.cryptic_game.backend.base.api.client;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ApiClientList {

    private final Set<ApiClient> clients;
    private final Set<Consumer<ApiClient>> clientAddedCallbacks;
    private final Set<Consumer<ApiClient>> clientRemovedCallbacks;

    public ApiClientList() {
        this.clients = new HashSet<>();
        this.clientAddedCallbacks = new HashSet<>();
        this.clientRemovedCallbacks = new HashSet<>();
    }

    public void add(final Channel channel) {
        final ApiClient client = new ApiClient(channel);
        this.clients.add(client);
        this.clientAddedCallbacks.forEach((consumer -> consumer.accept(client)));
    }

    public void remove(final ApiClient client) {
        this.clients.removeIf((c) -> c.getChannel().equals(client.getChannel()));
        this.clientRemovedCallbacks.forEach(consumer -> consumer.accept(client));
    }

    public ApiClient get(final Channel channel) {
        for (final ApiClient client : this.clients) {
            if (client.getChannel().equals(channel)) return client;
        }
        return null;
    }

    public Set<ApiClient> filter(final Predicate<ApiClient> predicate) {
        return this.clients.stream().filter(predicate).collect(Collectors.toSet());
    }

    public Set<ApiClient> getApiClients() {
        return this.clients;
    }

    public void registerClientAddedCallback(Consumer<ApiClient> consumer) {
        this.clientAddedCallbacks.add(consumer);
    }

    public void registerClientRemovedCallback(Consumer<ApiClient> consumer) {
        this.clientRemovedCallbacks.add(consumer);
    }
}
