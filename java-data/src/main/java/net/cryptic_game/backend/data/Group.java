package net.cryptic_game.backend.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum Group implements JsonSerializable {

    USER("user", "User", new Group[0], Permission.INTERNAL),
    CONTENT("content", "Content", new Group[]{USER}, Permission.TEAM_MANAGEMENT, Permission.FAQ_MANAGEMENT),
    MODERATOR("moderator", "Moderator", new Group[]{USER}, Permission.USER_MANAGEMENT),
    SERVER_ADMIN("server_admin", "Server Admin", new Group[]{USER}, Permission.SERVER_MANAGEMENT),
    ADMIN("admin", "Administrator", new Group[]{CONTENT, MODERATOR, SERVER_ADMIN}, Permission.ACCESS_MANAGEMENT);

    private static final Map<String, Group> GROUPS = new HashMap<>();

    static {
        for (final Group value : Group.values()) {
            GROUPS.put(value.getId(), value);
        }
    }

    private final String id;
    private final String displayName;
    private final int[] permissions;

    Group(final String id, final String displayName, final Group[] children, final int... permissions) {
        this.id = id;
        this.displayName = displayName;

        final List<Integer> collect = Arrays.stream(children).flatMapToInt(group -> Arrays.stream(group.getPermissions())).boxed().collect(Collectors.toList());
        for (int permission : permissions) collect.add(permission);

        this.permissions = new int[collect.size()];
        for (int i = 0; i < collect.size(); i++) this.permissions[i] = collect.get(i);
    }

    public static Group byId(final String id) {
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
