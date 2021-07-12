package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.util.HashSet;
import java.util.Map;

@Data
public class ApiEndpointCollectionData implements JsonSerializable {

    private final String id;
    private final String description;
    private final ApiType type;
    private final boolean internal;
    private final boolean disabled;
    private final Map<String, ApiEndpointData> endpoints;

    @Override
    public final JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("description", this.description)
                .add("endpoints", new HashSet<>(this.endpoints.values()))
                .add("disabled", this.disabled)
                .build();
    }
}
