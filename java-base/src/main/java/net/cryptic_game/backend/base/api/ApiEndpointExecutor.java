package net.cryptic_game.backend.base.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class ApiEndpointExecutor {

    protected final String name;

    protected final ApiCollection apiCollection;
    protected final Method method;
    private final List<ApiParameterData> parameters;

    public ApiEndpointExecutor(final String name, final ApiCollection apiCollection, final Method method) throws ApiParameterException {
        if (apiCollection.getName() != null) this.name = apiCollection.getName() + "/" + name; else this.name = name;
        this.apiCollection = apiCollection;
        this.method = method;
        this.parameters = this.loadParameters(this.method);
    }

    protected abstract List<ApiParameterData> loadParameters(final Method method) throws ApiParameterException;

    public abstract JsonObject execute(ApiExecutionData data) throws ApiException;

    protected Object[] validateParameters(final JsonObject json) throws ApiException {
        final Object[] parameterValues = new Object[this.parameters.size()];

        for (int i = 0; i < this.parameters.size(); i++) {
            final ApiParameterData parameter = this.parameters.get(i);

            if (!json.has(parameter.getKey())) {
                if (!parameter.isOptional()) {
                    throw new ApiParameterException("MISSING_PARAMETER_" + parameter.getKey().toUpperCase());
                }
                continue;
            }

            if (parameter.getType() == String.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsString();
            } else if (parameter.getType() == int.class || parameter.getType() == Integer.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsInt();
            } else if (parameter.getType() == long.class || parameter.getType() == Long.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsLong();
            } else if (parameter.getType() == double.class || parameter.getType() == Double.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsDouble();
            } else if (parameter.getType() == float.class || parameter.getType() == Float.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsFloat();
            } else if (parameter.getType() == boolean.class || parameter.getType() == Boolean.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsBoolean();
            } else if (parameter.getType() == Date.class) {
                parameterValues[i] = JsonUtils.getDate(json, parameter.getKey());
            } else if (parameter.getType() == JsonObject.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsJsonObject();
            } else if (parameter.getType() == JsonArray.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsJsonArray();
            } else if (parameter.getType() == JsonElement.class) {
                parameterValues[i] = json.get(parameter.getKey()).getAsJsonArray();
            } else if (parameter.getType() == UUID.class) {
                try {
                    parameterValues[i] = UUID.fromString(json.get(parameter.getKey()).getAsString());
                } catch (IllegalArgumentException e) {
                    throw new ApiParameterException("UUID_SYNTAX_ERROR");
                }

            } else {
                throw new ApiException("UNSUPPORTED_PARAMETER_" + parameter.getKey().toUpperCase() + "_" + parameter.getType().getTypeName().toUpperCase());
            }
        }

        return parameterValues;
    }


    public String getName() {
        return name;
    }
}
