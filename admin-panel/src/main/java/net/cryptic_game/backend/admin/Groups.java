package net.cryptic_game.backend.admin;

import lombok.Getter;
import net.cryptic_game.backend.base.api.Group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Groups implements Group {

    USER("user", "User"),
    CONTENT("content", "Content", USER),
    MODERATOR("moderator", "Moderator", USER),
    SERVER_ADMIN("server_admin", "Server Admin", USER),
    ADMIN("admin", "Administrator", CONTENT, MODERATOR, SERVER_ADMIN);

    public static final String USER_ID = "user";
    public static final String CONTENT_ID = "content";
    public static final String MODERATOR_ID = "moderator";
    public static final String SERVER_ADMIN_ID = "server_admin";
    public static final String ADMIN_ID = "admin";

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
}
