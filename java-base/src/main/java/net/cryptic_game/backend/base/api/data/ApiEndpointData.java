package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Setter
@AllArgsConstructor
public class ApiEndpointData implements JsonSerializable, Comparable<ApiEndpointData> {

    @NotNull
    private String id;
    @NotNull
    private final Set<Group> groups;
    @NotNull
    private final String description;
    @NotNull
    private List<ApiParameterData> parameters;
    private final boolean enabled;

    @NotNull
    private Object instance;
    @NotNull
    private Class<?> clazz;
    @NotNull
    private Method method;

    private final boolean normalParameters;

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder
                .create("id", this.id)
                .add("description", this.description)
                .add("parameters", this.parameters.stream()
                        .filter(parameter -> parameter.getType().equals(ApiParameterType.NORMAL))
                        .collect(Collectors.toUnmodifiableList()))
                .add("enabled", this.enabled)
                .build();
    }

    @Override
    public int compareTo(@NotNull final ApiEndpointData endpointData) {
        return this.id.compareTo(endpointData.id);
    }
}
