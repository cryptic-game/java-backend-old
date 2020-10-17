package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.Data;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ApiEndpointData implements JsonSerializable {

    private final String id;
    private final String description;
    private final ApiParameterData[] parameters;
    private final int authentication;
    private final boolean enabled;

    private final ApiAuthenticator authenticator;

    private final Object instance;
    private final Class<?> clazz;
    private final Method method;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.id)
                .add("description", this.description)
                .add("parameters", this.getNotmalParameters())
                .add("enabled", this.enabled)
                .build();
    }

    private List<ApiParameterData> getNotmalParameters() {
        return Arrays.stream(this.parameters)
                .filter(parameter -> parameter.getType().equals(ApiParameterType.NORMAL))
                .collect(Collectors.toList());
    }
}
