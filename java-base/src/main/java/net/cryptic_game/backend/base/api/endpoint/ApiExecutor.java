package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class ApiExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExecutor.class);

    private ApiExecutor() {
        throw new UnsupportedOperationException();
    }

    public static ApiResponse execute(final Map<String, ApiEndpointData> endpoints, final JsonObject request, final ApiClient client, final String tag) {
        if (!request.has("endpoint")) return new ApiResponse(ApiResponseType.BAD_REQUEST, "MISSING_ENDPOINT");
        else {
            final ApiEndpointData endpoint = endpoints.get(JsonUtils.fromJson(request.get("endpoint"), String.class));
            if (endpoint == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "ENDPOINT");
            else return executeMethod(endpoint,
                    JsonUtils.fromJson(request.get("data"), JsonObject.class),
                    client,
                    tag);
        }
    }

    private static ApiResponse executeMethod(final ApiEndpointData methodData, final JsonObject data, final ApiClient client, final String tag) {
        try {
            return (ApiResponse) methodData.getMethod().invoke(methodData.getObject(), getParameters(methodData.getParameters(),
                    methodData.isNormalParameters(),
                    data == null ? new JsonObject() : data,
                    client,
                    tag,
                    methodData));
        } catch (ApiException e) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            LOG.error("Error while executing endpoint \"" + methodData.getName() + "\".", e);
            return new ApiResponse(ApiResponseType.INTERNAL_SERVER_ERROR);
        }
    }

    private static Object[] getParameters(
            final List<ApiParameterData> parameters,
            final boolean normalParameters,
            final JsonObject data,
            final ApiClient client,
            final String tag,
            final ApiEndpointData endpoint
    ) throws ApiException {
        int size = 0;
        for (final ApiParameterData parameter : parameters) {
            if (parameter.getSpecial().equals(ApiParameterSpecialType.NORMAL) || parameter.getSpecial().equals(ApiParameterSpecialType.USER)) {
                if (normalParameters) size++;
            } else size++;
        }

        final Object[] objects = new Object[size];
        int current = 0;

        for (final ApiParameterData parameter : parameters) {
            switch (parameter.getSpecial()) {
                case NORMAL:
                    if (!parameter.isOptional() && (!data.has(parameter.getName()) || data.get(parameter.getName()).isJsonNull())) {
                        throw new ApiException("Parameter \"" + parameter.getName() + "\" is missing.");
                    } else if (normalParameters) {
                        try {
                            objects[current] = JsonUtils.fromJson(data.get(parameter.getName()), parameter.getJavaType());
                        } catch (JsonTypeMappingException e) {
                            throw new ApiException("Invalid format of parameter \"" + parameter.getName() + "\".", e);
                        }
                    }
                    break;
                case TAG:
                    objects[current] = tag;
                    break;
                case CLIENT:
                    objects[current] = client;
                    break;
                case ENDPOINT:
                    objects[current] = endpoint;
                    break;
                case DATA:
                    objects[current] = data;
                    break;
                case USER:
                    if (!parameter.isOptional() && (!data.has(parameter.getName()) || data.get(parameter.getName()).isJsonNull())) {
                        throw new ApiException("Parameter \"" + parameter.getName() + "\" is missing.");
                    } else if (normalParameters) {
                        try {
                            objects[current] = JsonUtils.fromJson(data.get(parameter.getName()), parameter.getJavaType());
                        } catch (JsonTypeMappingException e) {
                            throw new ApiException("Invalid format of parameter \"" + parameter.getName() + "\".", e);
                        }
                    }
            }
            current++;
        }

        return objects;
    }
}
