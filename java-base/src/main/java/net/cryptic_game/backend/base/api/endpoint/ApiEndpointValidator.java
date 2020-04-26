package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

final class ApiEndpointValidator {

    private static final Logger log = LoggerFactory.getLogger(ApiEndpointValidator.class);

    private ApiEndpointValidator() {
    }

    static boolean validateMethod(final ApiEndpointCollection apiCollection, final String name, final Method method) {
        if (method.getReturnType() != ApiResponse.class) {
            log.warn("Api \"" + apiCollection.getName() + "/" + name + "\" has not the return type \"" + ApiResponse.class.getName() + "\".");
            return false;
        }
        return true;
    }

    static Object validateParameters(final String name,
                                     final List<ApiEndpointParameterData> parameters,
                                     final JsonObject json,
                                     final boolean array) throws ApiException {
        Object[] parameterValues = new Object[0];
        if (array) parameterValues = new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            final ApiEndpointParameterData parameter = parameters.get(i);

            if (!json.has(parameter.getKey())) {
                if (!parameter.isOptional()) {
                    throw new ApiEndpointParameterException("Can't find parameter \"" + parameter.getKey() + "\".");
                }
                continue;
            }

            if (parameter.getParameters() == null) {
                if (array) {
                    try {
                        if (parameter.getType() == String.class || parameter.getType() == null) {
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
                        } else if (parameter.getType() == LocalDate.class) {
                            parameterValues[i] = LocalDate.ofInstant(Instant.ofEpochMilli(json.get(parameter.getKey()).getAsLong()), ZoneOffset.UTC);
                        } else if (parameter.getType() == LocalDateTime.class) {
                            parameterValues[i] = LocalDateTime.ofInstant(Instant.ofEpochMilli(json.get(parameter.getKey()).getAsLong()), ZoneOffset.UTC);
                        } else if (parameter.getType() == JsonObject.class) {
                            parameterValues[i] = json.get(parameter.getKey()).getAsJsonObject();
                        } else if (parameter.getType() == JsonArray.class) {
                            parameterValues[i] = json.get(parameter.getKey()).getAsJsonArray();
                        } else if (parameter.getType() == JsonElement.class) {
                            parameterValues[i] = json.get(parameter.getKey()).getAsJsonArray();
                        } else if (parameter.getType() == UUID.class) {
                            parameterValues[i] = UUID.fromString(json.get(parameter.getKey()).getAsString());
                        } else {
                            throw new ApiException("Not supported parameter. (\"" + name + ":" + parameter.getKey() + "\") type \"" + parameter.getType().getName() + "\"");
                        }
                    } catch (NumberFormatException ignored) {
                        return null;
                    }
                } else {
                    return json;
                }
            } else {
                parameterValues[i] = ApiEndpointValidator.validateParameters(name, parameter.getParameters(), json.get(parameter.getKey()).getAsJsonObject(), false);
            }
        }

        return parameterValues;
    }
}
