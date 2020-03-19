package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.base.utils.JsonUtils;

import java.util.HashSet;
import java.util.Set;

public class DaemonRegisterPacket implements JsonSerializable {

    private final String name;
    private final Set<Function> functions;

    public DaemonRegisterPacket(final String name) {
        this.name = name;
        this.functions = new HashSet<>();
    }

    public DaemonRegisterPacket(final JsonObject json, final Daemon daemon) {
        if (!(json.has("name") && json.has("functions"))) {
            throw new IllegalArgumentException("Missing \"name\" or \"functions\" property.");
        }

        this.name = json.get("name").getAsString();
        this.functions = new HashSet<>();

        final JsonArray jsonFunctions = json.get("arguments").getAsJsonArray();
        for (final JsonElement function : jsonFunctions) {
            if (!function.isJsonObject()) {
                throw new IllegalArgumentException("In the Function-Array (\"functions\"-property) is an Object witch is not a JsonObject.");
            }

            try {
                this.functions.add(new Function(function.getAsJsonObject(), daemon));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("name", this.getName())
                .add("functions", JsonUtils.toArray(this.functions))
                .build();
    }

    public String getName() {
        return this.name;
    }

    public void addFunction(final Function function) {
        this.functions.add(function);
    }

    public Set<Function> getFunctions() {
        return this.functions;
    }
}
