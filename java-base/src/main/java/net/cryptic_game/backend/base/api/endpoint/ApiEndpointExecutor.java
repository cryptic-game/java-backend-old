package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ApiEndpointExecutor {

    private static final Logger log = LoggerFactory.getLogger(ApiEndpointExecutor.class);

    private final String name;

    private final ApiEndpointCollection apiCollection;
    private final Method method;
    private final List<ApiEndpointParameterData> parameters;

    private boolean useClient;
    private boolean useTag;

    ApiEndpointExecutor(final String name, final ApiEndpointCollection apiCollection, final Method method) throws ApiEndpointParameterException {
        this.name = apiCollection.getName() + "/" + name;
        this.apiCollection = apiCollection;
        this.method = method;
        this.useClient = false;
        this.useTag = false;
        this.parameters = this.loadParameters(this.method);
    }

    ApiResponse execute(final ApiClient client, final String tag, final JsonObject json) throws ApiException {
        try {
            final Object result = this.validateParameters(this.parameters, json, true);
            if (result == null) return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_A_NUMBER");
            final Object[] parameters = (Object[]) result;
            Object[] methodArgs = null;

            if (this.useClient) {
                if (this.useTag) {
                    methodArgs = new Object[parameters.length + 2];
                    methodArgs[0] = client;
                    methodArgs[1] = tag;
                    System.arraycopy(parameters, 0, methodArgs, 2, parameters.length);
                } else {
                    methodArgs = new Object[parameters.length + 1];
                    methodArgs[0] = client;
                    System.arraycopy(parameters, 0, methodArgs, 1, parameters.length);
                }
            } else if (this.useTag) {
                methodArgs = new Object[parameters.length + 1];
                methodArgs[0] = tag;
                System.arraycopy(parameters, 0, methodArgs, 1, parameters.length);
            }

            final Object value = this.method.invoke(this.apiCollection, methodArgs == null ? parameters : methodArgs);
            if (value != null) return (ApiResponse) value;
            else return null;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApiException("Unable to execute JsonApi endpoint \"" + this.name + "\".", e);
        } catch (IllegalArgumentException e) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_PARAMETERS");
        }
    }

    private List<ApiEndpointParameterData> loadParameters(final Method method) throws ApiEndpointParameterException {
        final List<ApiEndpointParameterData> parameters = new ArrayList<>();

        for (final Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(ApiParameter.class)) {
                if (!parameter.isAnnotationPresent(ApiParameters.class)) {
                    if (parameter.isAnnotationPresent(ApiTag.class)) {
                        if (parameter.getType().equals(String.class)) this.useTag = true;
                        else {
                            throw new ApiEndpointParameterException("Api Parameter \"" + parameter.getName() + "\" of Api endpoint \"" + this.name + "\" with Annotation \"" + ApiTag.class.getName() + "\" does not have \"" + String.class.getName() + "\" as Type.");
                        }
                    } else if (parameter.getType().equals(ApiClient.class)) {
                        this.useClient = true;
                    } else {
                        throw new ApiEndpointParameterException("Api Parameter \"" + parameter.getName() + "\" of Api endpoint \"" + this.name + "\" does not have \"" + ApiParameter.class.getName() + "\" Annotation.");
                    }
                } else {
                    parameters.add(this.loadParameters(parameter.getAnnotation(ApiParameters.class)));
                }
            } else {
                final ApiParameter apiParameter = parameter.getAnnotation(ApiParameter.class);
                parameters.add(new ApiEndpointParameterData(apiParameter.value().toLowerCase(), parameter.getType(), null, apiParameter.optional()));
            }
        }
        return parameters;
    }

    private Object validateParameters(final List<ApiEndpointParameterData> parameters, final JsonObject json, final boolean array) throws ApiException {
        Object[] parameterValues = new Object[0];
        if (array) parameterValues = new Object[parameters.size()];

        for (int i = 0; i < parameters.size(); i++) {
            final ApiEndpointParameterData parameter = parameters.get(i);

            if (!json.has(parameter.getKey())) {
                if (parameter.isOptional()) {
                    continue;
                } else {
                    throw new ApiEndpointParameterException("Can't find parameter \"" + parameter.getKey() + "\".");
                }
            }

            if (parameter.getParameters() == null) {
                if (array) try {
                    parameterValues[i] = JsonUtils.fromJson(json.get(parameter.getKey()), parameter.getType());
                } catch (JsonTypeMappingException e) {
                    throw new ApiException("Not supported parameter. (\"" + this.name + ":" + parameter.getKey() + "\") type \"" + parameter.getType().getName() + "\"", e);
                }
                else return json;
            } else {
                parameterValues[i] = this.validateParameters(parameter.getParameters(), json.get(parameter.getKey()).getAsJsonObject(), false);
            }
        }

        return parameterValues;
    }

    private ApiEndpointParameterData loadParameters(final ApiParameters apiParameters) {
        final List<ApiEndpointParameterData> parameterDataList = new ArrayList<>();
        for (final ApiParameter apiParameter : apiParameters.parameters()) {
            parameterDataList.add(new ApiEndpointParameterData(apiParameter.value(), null, null, apiParameter.optional()));
        }
        return new ApiEndpointParameterData(apiParameters.name(), null, parameterDataList, apiParameters.optional());
    }

    public String getName() {
        return this.name;
    }

    public List<ApiEndpointParameterData> getParameters() {
        return this.parameters;
    }
}
