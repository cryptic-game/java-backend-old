package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
public class ApiEndpointData implements JsonSerializable, Comparable<ApiEndpointData> {

    private final String description;
    private final int authentication;
    private final Class<?> clazz;
    private boolean disabled;
    private ApiAuthenticator authenticator;
    private String id;
    private ApiParameterData[] parameters;
    private Object instance;
    private Method method;

    @Override
    public final JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("description", this.description)
                .add("disabled", this.disabled)
                .build();
    }

    @Override
    public final int compareTo(@NotNull final ApiEndpointData other) {
        return this.id.compareTo(other.id);
    }
}
