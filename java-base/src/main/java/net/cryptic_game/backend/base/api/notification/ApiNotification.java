package net.cryptic_game.backend.base.api.notification;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;

import java.util.Set;
import java.util.stream.Collectors;

public class ApiNotification implements JsonSerializable {

    private final String id;
    private final Set<ApiClient> clients;
    private final NotificationTopic topic;
    private final JsonElement data;

    public ApiNotification(final String id, final Set<ApiClient> clients, final NotificationTopic topic, final JsonSerializable serializable) {
        this(id, clients, topic, serializable.serialize());
    }

    public ApiNotification(final String id, final Set<ApiClient> clients, final NotificationTopic topic, final JsonElement data) {
        this.id = id;
        this.clients = clients;
        this.topic = topic;
        this.data = data;
    }

    public ApiNotification(final String id, final JsonSerializable serializable) {
        this(id, serializable.serialize());
    }

    public ApiNotification(final String id, final JsonElement data) {
        this.id = id;
        this.data = data;
        this.clients = null;
        this.topic = null;
    }

    public void send(final ApiClient client) {
        client.writeAndFlush(this.serialize());
    }

    public void send(final Set<ApiClient> clients) {
        clients.forEach(this::send);
    }

    public void send() {
        if (this.topic == null || this.clients == null) {
            throw new ApiNotificationException("Topic or clients haven't been specified.");
        } else {
            this.send(this.clients.stream().filter(client -> client.hasSubscribed(this.topic)).collect(Collectors.toUnmodifiableSet()));
        }
    }

    String getId() {
        return id;
    }

    JsonElement getData() {
        return this.data;
    }

    @Override
    public JsonObject serialize() {
        return ApiNotificationBuilder.toJson(this);
    }
}
