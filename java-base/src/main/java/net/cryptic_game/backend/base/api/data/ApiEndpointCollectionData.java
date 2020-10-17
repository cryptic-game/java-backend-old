package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeSet;

@Data
public class ApiEndpointCollectionData implements JsonSerializable, Comparable<ApiEndpointCollectionData> {

    private final String id;
    private final String description;
    private final ApiType type;
    private final boolean disabled;
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("description", this.description)
                .add("endpoints", new TreeSet<>(this.endpoints.values()))
                .add("disabled", this.disabled)
                .build();
    }

    @Override
    public int compareTo(@NotNull final ApiEndpointCollectionData other) {
        return this.id.compareTo(other.id);
    }
}
