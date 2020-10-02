package net.cryptic_game.backend.admin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AdminPanelAuthenticationProvider implements ApiAuthenticationProvider {

    private final Key key;
    private final Set<Group> groups;

    public AdminPanelAuthenticationProvider(final Key key, final Group... groups) {
        this.key = key;
        this.groups = Set.of(groups);
    }

    @Override
    public Set<Group> getGroups() {
        return this.groups;
    }

    @Override
    public Set<Group> resolveGroups(final String jwt) {
        if (jwt == null) return Collections.emptySet();
        try {
            final Set<Group> groups = new HashSet<>();
            final JsonObject jsonObject = SecurityUtils.parseJwt(key, jwt);
            if (!jsonObject.has("exp") || JsonUtils.fromJson(jsonObject.get("exp"), OffsetDateTime.class).isBefore(OffsetDateTime.now())) {
                return Collections.emptySet();
            }
            JsonUtils.fromJson(jsonObject.get("groups"), JsonArray.class)
                    .forEach(group -> groups.add(Groups.byId(JsonUtils.fromJson(group, String.class))));
            return groups;
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean isPermitted(final Set<Group> required, final Set<Group> provided) {
        if (required.size() == 0) return true;
        for (final Group group1 : provided) {
            for (final Group group2 : required) {
                if (group1.equals(group2)) return true;
                if (group1.getChildren().contains(group2)) return true;
            }
        }
        return false;
    }
}
