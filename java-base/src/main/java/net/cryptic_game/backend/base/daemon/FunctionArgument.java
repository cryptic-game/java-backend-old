package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.utils.JsonBuilder;

public class FunctionArgument implements JsonSerializable {

    private final String name;
    private final boolean required;
    private final FunctionArgumentType type;

    public FunctionArgument(final String name, final boolean required, final FunctionArgumentType type) {
        this.name = name;
        this.required = required;
        this.type = type;
    }

    public FunctionArgument(final JsonObject json) {
        if (!(json.has("name") && json.has("required") && json.has("type"))) {
            throw new IllegalArgumentException("Missing \"name\" or \"required\" or \"type\" property.");
        }

        this.name = json.get("name").getAsString();
        this.required = json.get("required").getAsBoolean();
        this.type = FunctionArgumentType.valueOf(json.get("type").getAsString().toUpperCase());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("name", this.getName())
                .add("required", this.isRequired())
                .add("type", this.getType().name().toLowerCase())
                .build();
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }

    public FunctionArgumentType getType() {
        return this.type;
    }
}
