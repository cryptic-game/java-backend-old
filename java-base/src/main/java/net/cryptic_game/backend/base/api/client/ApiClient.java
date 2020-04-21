package net.cryptic_game.backend.base.api.client;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ApiClient {

    private final Channel channel;
    private final Set<String> topics;
    private final Set<Object> objects;

    ApiClient(final Channel channel) {
        this.channel = channel;
        this.topics = new HashSet<>();
        this.objects = new HashSet<>();
    }

    public void writeAndFlush(final JsonObject json) {
        this.channel.writeAndFlush(json);
    }

    public void add(final Object object) {
        this.objects.add(object);
    }

    public <T> T get(final Class<? extends T> type) {
        for (final Object object : this.objects) {
            if (object.getClass().equals(type)) return type.cast(object);
        }
        return null;
    }

    public void subscribe(final String topic) {
        this.topics.add(topic);
    }

    public void unsubscribe(final String topic) {
        this.topics.remove(topic);
    }

    public boolean hasSubscribed(final String topic) {
        return this.topics.contains(topic);
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiClient)) return false;
        final ApiClient apiClient = (ApiClient) o;
        return getChannel().equals(apiClient.getChannel()) &&
                this.topics.equals(apiClient.topics) &&
                this.objects.equals(apiClient.objects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannel(), this.topics, this.objects);
    }
}
