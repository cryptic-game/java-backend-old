package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Setter;
import net.cryptic_game.backend.base.api.data.ApiEndpointCollectionData;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.DaemonEndpointCollectionData;
import net.cryptic_game.backend.base.daemon.DaemonEndpointData;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class DaemonUtils {

    @Setter
    private static String serverAddress;

    private DaemonUtils() {
        throw new UnsupportedOperationException();
    }

    public static void notifyUser(@NotNull final UUID user, @NotNull final Enum<?> topic, @Nullable final Object data) {
        notifyUser(user, topic.name(), data);
    }

    public static void notifyUser(@NotNull final UUID user, @NotNull final String topic, @Nullable final Object data) {
//        HttpClientUtils.sendAsyncRequest(serverAddress + "/daemon/notify", JsonBuilder.create("user_id", user).add("topic", topic).add("data", data).build());
        throw new NotYetImplementedException();
    }

    @NotNull
    public static Set<ApiEndpointCollectionData> parseDaemonEndpoints(@NotNull final Daemon daemon, @NotNull final JsonArray collections, @NotNull final ApiType apiType) {
        return JsonUtils.fromArray(collections, new HashSet<>(), json -> {
            final JsonObject jsonObject = JsonUtils.fromJson(json, JsonObject.class);

            final String id = JsonUtils.fromJson(jsonObject.get("id"), String.class);
            final String description = JsonUtils.fromJson(jsonObject.get("description"), String.class);
            final boolean disabled = JsonUtils.fromJson(jsonObject.get("disabled"), boolean.class);

            final Map<String, ApiEndpointData> endpoints = JsonUtils.fromArray(
                    JsonUtils.fromJson(jsonObject.get("endpoints"), JsonArray.class),
                    new HashSet<>(),
                    DaemonEndpointData.class
            )
                    .stream()
                    .peek(endpoint -> {
                        endpoint.setDaemon(daemon);
                    })
                    .collect(Collectors.toUnmodifiableMap(ApiEndpointData::getId, endpoint -> endpoint));

            final DaemonEndpointCollectionData collection = new DaemonEndpointCollectionData(id, description, disabled, apiType, endpoints);
            collection.setDaemon(daemon);
            return collection;
        });
    }
}
