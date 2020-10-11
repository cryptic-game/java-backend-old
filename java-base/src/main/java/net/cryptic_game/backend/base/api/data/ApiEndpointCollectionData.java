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

//    @NotNull
    private final String id;
    @NotNull
    private final String description;
    @NotNull
    private final ApiType apiType;
    @NotNull
    private final Map<String, ApiEndpointData> endpoints;

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder
                .create("id", this.id)
                .add("description", this.description)
                .add("endpoints", new TreeSet<>(this.endpoints.values()))
                .build();
    }

    @Override
    public int compareTo(@NotNull final ApiEndpointCollectionData collectionData) {
        return this.id.compareTo(collectionData.id);
    }
}
