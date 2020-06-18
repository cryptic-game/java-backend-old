package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.json.JsonTransient;

import java.util.Map;
import java.util.Objects;

public final class ApiEndpointCollectionData implements JsonSerializable, Comparable<ApiEndpointCollectionData> {

    private final String name;
    private final String description;
    @JsonTransient
    private final ApiEndpointCollection object;
    private Map<String, ApiEndpointData> endpoints;

    public ApiEndpointCollectionData(final String name, final String description, final ApiEndpointCollection object, final Map<String, ApiEndpointData> endpoints) {
        this.name = name;
        this.description = description;
        this.object = object;
        this.endpoints = endpoints;
    }

    public JsonElement serialize() {
        return JsonBuilder.create("name", this.name)
                .add("description", this.description)
                .add("endpoints", this.endpoints.values())
                .build();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public ApiEndpointCollection getObject() {
        return this.object;
    }

    public Map<String, ApiEndpointData> getEndpoints() {
        return this.endpoints;
    }

    public void setEndpoints(final Map<String, ApiEndpointData> endpoints) {
        this.endpoints = endpoints;
    }

    public String toString() {
        return "ApiEndpointCollectionData(name=" + this.getName() + ", description=" + this.getDescription()
                + ", object=" + this.getObject() + ", endpoints=" + this.getEndpoints() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiEndpointCollectionData)) return false;
        ApiEndpointCollectionData that = (ApiEndpointCollectionData) o;
        return getName().equals(that.getName())
                && getDescription().equals(that.getDescription())
                && getObject().equals(that.getObject())
                && getEndpoints().equals(that.getEndpoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getObject(), getEndpoints());
    }

    @Override
    public int compareTo(final ApiEndpointCollectionData object) {
        return this.getName().compareTo(object.getName());
    }
}
