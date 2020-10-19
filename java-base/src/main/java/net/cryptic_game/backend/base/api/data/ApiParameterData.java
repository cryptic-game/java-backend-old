package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

@Data
@AllArgsConstructor
@RequiredArgsConstructor(access = AccessLevel.NONE)
public final class ApiParameterData implements JsonSerializable {

    private final String id;
    private final boolean required;
    private final String description;
    private final Class<?> classType;
    private ApiParameterType type;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("required", this.required)
                .add("description", this.description)
                .build();
    }
}
