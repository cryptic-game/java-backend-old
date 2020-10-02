package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.cryptic_game.backend.base.api.Group;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@Data
@AllArgsConstructor
public final class ApiRequest {

    @Nullable
    private final String endpoint;
    @Nullable
    private final JsonObject data;
    @Nullable
    private Set<Group> authenticationGroups;
    @Nullable
    private String tag;

    public ApiRequest(final @Nullable String endpoint, final @Nullable JsonObject data, final @Nullable String tag) {
        this.endpoint = endpoint;
        this.data = data;
        this.tag = tag;
    }
}
