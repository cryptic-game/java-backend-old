package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

@Data
public final class ApiParameterData implements JsonSerializable {

    private final String id;
    private final boolean required;
    private final ApiParameterType type;
    private final String description;
    private final Class<?> classType;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("required", this.required)
                .add("description", this.description)
                .build();
    }
}
