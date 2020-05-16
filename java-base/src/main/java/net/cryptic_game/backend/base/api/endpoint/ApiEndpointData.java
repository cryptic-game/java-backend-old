package net.cryptic_game.backend.base.api.endpoint;


import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("name", this.name)
                .add("description", this.description)
                .add("parameters", this.parameters.stream().filter(parameter -> parameter.getSpecial().equals(ApiParameterSpecialType.NORMAL)).collect(Collectors.toList()))
                .build();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(final Object object) {
        this.object = object;
    }

    public boolean isNormalParameters() {
        return this.normalParameters;
    }

    public void setNormalParameters(final boolean normalParameters) {
        this.normalParameters = normalParameters;
    }

    public List<ApiParameterData> getParameters() {
        return this.parameters;
    }

    public void setParameters(final List<ApiParameterData> parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return "ApiEndpointData(name=" + this.getName() + ", description=" + this.getDescription() + ", method=" + this.getMethod() + ", object=" + this.getObject() + ", parameters=" + this.getParameters() + ")";
    }

    @Override
    public int compareTo(final ApiEndpointData object) {
        return this.getName().compareTo(object.getName());
    }

    protected ApiEndpointData copy() {
        final ApiEndpointData endpointData = new ApiEndpointData(this.name, this.description, this.method, this.object, this.parameters);
        endpointData.setNormalParameters(this.normalParameters);
        return endpointData;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiEndpointData)) return false;
        ApiEndpointData that = (ApiEndpointData) o;
        return getName().equals(that.getName()) &&
                getDescription().equals(that.getDescription()) &&
                getMethod().equals(that.getMethod()) &&
                getObject().equals(that.getObject()) &&
                getParameters().equals(that.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getMethod(), getObject(), getParameters());
    }
}
