package net.cryptic_game.backend.base.api.endpoint;

import net.cryptic_game.backend.base.json.JsonTransient;

import java.util.Objects;

public final class ApiParameterData {

    private final String name;
    private final String description;
    private final boolean optional;
    @JsonTransient
    private final Class<?> javaType;
    private final String type;
    @JsonTransient
    private ApiParameterSpecialType special;

    public ApiParameterData(final String name, final ApiParameterSpecialType special, final String description, final boolean optional, final Class<?> javaType) {
        this.name = name;
        this.special = special;
        this.description = description;
        this.optional = optional;
        this.javaType = javaType;
        this.type = this.getTypeAsString();
    }

    private String getTypeAsString() {
        if (!Number.class.isAssignableFrom(this.getJavaType())
                && !Integer.TYPE.isAssignableFrom(this.getJavaType())
                && !Long.TYPE.isAssignableFrom(this.getJavaType())
                && !Double.TYPE.isAssignableFrom(this.getJavaType())
                && !Byte.TYPE.isAssignableFrom(this.getJavaType())
                && !Short.TYPE.isAssignableFrom(this.getJavaType())
                && !Float.TYPE.isAssignableFrom(this.getJavaType())) {
            return !Boolean.class.isAssignableFrom(this.getJavaType()) && !Boolean.TYPE.isAssignableFrom(this.getJavaType()) ? "string" : "boolean";
        } else {
            return "number";
        }
    }

    public String getName() {
        return this.name;
    }

    public ApiParameterSpecialType getSpecial() {
        return this.special;
    }

    public void setSpecial(final ApiParameterSpecialType special) {
        this.special = special;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public Class<?> getJavaType() {
        return this.javaType;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiParameterData)) return false;
        ApiParameterData that = (ApiParameterData) o;
        return isOptional() == that.isOptional()
                && getName().equals(that.getName())
                && getSpecial() == that.getSpecial()
                && getDescription().equals(that.getDescription())
                && getJavaType().equals(that.getJavaType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getSpecial(), getDescription(), isOptional(), getJavaType());
    }

    public String toString() {
        return "ApiParameterData(name=" + this.getName() + ", special=" + this.getSpecial() + ", description="
                + this.getDescription() + ", optional=" + this.isOptional() + ", type=" + this.getJavaType() + ")";
    }
}
