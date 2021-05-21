package net.cryptic_game.backend.endpoints;

import com.google.gson.JsonElement;

import java.util.UUID;

public interface NotificationService {

    void sendNotification(UUID userId, String topic, JsonElement data);
}
