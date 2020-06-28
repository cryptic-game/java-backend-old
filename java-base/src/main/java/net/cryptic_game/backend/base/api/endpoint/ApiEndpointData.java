package net.cryptic_game.backend.base.api.endpoint;


import com.google.gson.JsonElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
public class ApiEndpointData implements JsonSerializable, Comparable<ApiEndpointData> {

    private final String description;
    private String name;
    private Method method;
    private Object object;
    private boolean normalParameters;
    private List<ApiParameterData> parameters;

    public ApiEndpointData(final String name, final String description, final Method method, final Object object, final List<ApiParameterData> parameters) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.object = object;
        this.normalParameters = true;
        this.parameters = parameters;
    }

    /**
     * Creates a Json Representation of this object.
     *
     * @return a {@link JsonElement} with the Json Representation
     */
    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("name", this.name)
                .add("description", this.description)
                .add("parameters", this.parameters.stream().filter(parameter -> parameter.getSpecial().equals(ApiParameterSpecialType.NORMAL)).collect(Collectors.toList()))
                .build();
    }

    @Override
    public final int compareTo(final ApiEndpointData object) {
        return this.getName().compareTo(object.getName());
    }

    protected final ApiEndpointData copy() {
        final ApiEndpointData endpointData = new ApiEndpointData(this.name, this.description, this.method, this.object, this.parameters);
        endpointData.setNormalParameters(this.normalParameters);
        return endpointData;
    }
}
