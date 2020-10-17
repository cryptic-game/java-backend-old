package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ApiEndpointData implements JsonSerializable, Comparable<ApiEndpointData> {

    private final String description;
    private final int authentication;
    private final boolean disabled;
    private final Class<?> clazz;
    private ApiAuthenticator authenticator;
    private String id;
    private ApiParameterData[] parameters;
    private Object instance;
    private Method method;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("description", this.description)
                .add("parameters", this.getNotmalParameters())
                .add("disabled", this.disabled)
                .build();
    }

    private List<ApiParameterData> getNotmalParameters() {
        return Arrays.stream(this.parameters)
                .filter(parameter -> parameter.getType().equals(ApiParameterType.NORMAL))
                .collect(Collectors.toList());
    }

    @Override
    public int compareTo(@NotNull final ApiEndpointData other) {
        return this.id.compareTo(other.id);
    }
}
