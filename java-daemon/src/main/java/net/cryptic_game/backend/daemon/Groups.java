package net.cryptic_game.backend.daemon;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Groups implements Group, JsonSerializable {

    AUTHORIZED("authorized", "Authorized");

    private static final Map<String, Groups> GROUPS = new HashMap<>();

    static {
        for (final Groups value : Groups.values()) {
            GROUPS.put(value.getId(), value);
        }
    }

    private final String id;
    private final String displayName;
    private final Set<Group> children;

    Groups(final String id, final String displayName, final Group... children) {
        this.id = id;
        this.displayName = displayName;
        this.children = Stream.concat(Arrays.stream(children), Arrays.stream(children).flatMap(child -> child.getChildren().stream())).collect(Collectors.toSet());
    }

    public static Groups byId(final String id) {
        return GROUPS.get(id);
    }

    @Override
    public String toString() {
        return this.id;
    }

    @Override
    public JsonElement serialize() {
        return new JsonPrimitive(this.id);
    }
}
