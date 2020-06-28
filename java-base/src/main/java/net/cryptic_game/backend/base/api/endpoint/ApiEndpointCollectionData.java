package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ApiEndpointCollectionData implements JsonSerializable, Comparable<ApiEndpointCollectionData> {

    private final String name;
    private final String description;
    private final ApiEndpointCollection object;
    private Map<String, ApiEndpointData> endpoints;

    /**
     * Creates a Json Representation of this object.
     *
     * @return a {@link JsonElement} with the Json Representation
     */
    public JsonElement serialize() {
        return JsonBuilder.create("name", this.name)
                .add("description", this.description)
                .add("endpoints", this.endpoints.values())
                .build();
    }

    @Override
    public final int compareTo(final ApiEndpointCollectionData object) {
        return this.getName().compareTo(object.getName());
    }
}
