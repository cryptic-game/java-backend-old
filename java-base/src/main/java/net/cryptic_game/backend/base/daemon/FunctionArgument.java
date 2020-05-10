package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

public class FunctionArgument implements JsonSerializable {

    private final String name;
    private final boolean required;

    public FunctionArgument(final String name, final boolean required) {
        this.name = name;
        this.required = required;
    }

    public FunctionArgument(final JsonObject json) {
        if (!(json.has("name") && json.has("required"))) {
            throw new IllegalArgumentException("Missing \"name\" or \"required\" property.");
        }

        this.name = json.get("name").getAsString();
        this.required = json.get("required").getAsBoolean();
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("name", this.getName())
                .add("required", this.isRequired())
                .build();
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }
}
