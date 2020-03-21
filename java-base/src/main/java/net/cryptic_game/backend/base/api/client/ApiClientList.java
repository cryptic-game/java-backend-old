package net.cryptic_game.backend.base.api.client;

import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ApiClientList {

    private final Set<ApiClient> clients;

    public ApiClientList() {
        this.clients = new HashSet<>();
    }

    public void add(final Channel channel) {
        this.clients.add(new ApiClient(channel));
    }

    public void remove(final ApiClient client) {
        this.clients.remove(client);
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
}
