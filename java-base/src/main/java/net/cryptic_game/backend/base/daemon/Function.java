package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.JsonUtils;

import java.util.HashSet;
import java.util.Set;

public class Function implements JsonSerializable {

    private final String name;
    private final Daemon daemon;
    private final Set<FunctionArgument> arguments;

    public Function(final String name, final Daemon daemon, final Set<FunctionArgument> arguments) {
        this.name = name;
        this.daemon = daemon;
        this.arguments = arguments;
    }

    public Function(final JsonObject json, final Daemon daemon) throws IllegalArgumentException {
        if (!(json.has("name") && json.has("arguments"))) {
            throw new IllegalArgumentException("Missing \"name\" or \"arguments\" property.");
        }

        this.name = json.get("name").getAsString();
        this.daemon = daemon;
        this.arguments = new HashSet<>();

        final JsonArray jsonArguments = json.get("arguments").getAsJsonArray();
        for (final JsonElement argument : jsonArguments) {
            if (!argument.isJsonObject()) {
                throw new IllegalArgumentException("In the Function-Arguments-Array (\"arguments\"-property) is an Object witch is not a JsonObject.");
            }

            try {
                this.arguments.add(new FunctionArgument(argument.getAsJsonObject()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("name", this.getName())
                .add("arguments", JsonUtils.toArray(this.getArguments()))
                .build();
    }

    public String getName() {
        return this.name;
    }

    public Daemon getDaemon() {
        return this.daemon;
    }

    public Set<FunctionArgument> getArguments() {
        return this.arguments;
    }
}
