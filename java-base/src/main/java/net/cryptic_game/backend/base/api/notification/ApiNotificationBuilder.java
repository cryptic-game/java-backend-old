package net.cryptic_game.backend.base.api.notification;

import com.google.gson.JsonObject;

final class ApiNotificationBuilder {

    static JsonObject toJson(final ApiNotification notification) {
        final JsonObject json = new JsonObject();

        final JsonObject info = new JsonObject();
        info.addProperty("notification", true);
        info.addProperty("id", notification.getId());
        json.add("info", info);

        json.add("data", notification.getData());

        return json;
    }
}
