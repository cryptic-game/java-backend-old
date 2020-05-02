package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.base.json.JsonBuilder;

import java.util.HashSet;
import java.util.Set;

public class DaemonRegisterPacket implements JsonSerializable {

    private final Daemon daemon;
    private final String name;
    private final Set<Function> functions;

    public DaemonRegisterPacket(final String name) {
        this.daemon = null;
        this.name = name;
        this.functions = new HashSet<>();
    }

    public DaemonRegisterPacket(final Channel channel, final String name, final JsonArray jsonFunctions) {
        this.daemon = new Daemon(channel, name);
        this.name = name;
        this.functions = new HashSet<>();

        for (final JsonElement function : jsonFunctions) {
            if (!function.isJsonObject()) {
                throw new IllegalArgumentException("In the Function-Array (\"functions\"-property) is an Object witch is not a JsonObject.");
            }

            try {
                this.functions.add(new Function(function.getAsJsonObject(), this.daemon));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("name", this.getName())
                .add("functions", this.functions)
                .build();
    }

    public Daemon getDaemon() {
        return this.daemon;
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
