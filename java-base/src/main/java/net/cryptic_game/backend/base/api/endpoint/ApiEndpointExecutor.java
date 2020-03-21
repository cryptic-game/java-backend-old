package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

class ApiEndpointExecutor {

    private static final Logger log = LoggerFactory.getLogger(ApiEndpointExecutor.class);

    private final String name;

    private final ApiEndpointCollection apiCollection;
    private final Method method;
    private final List<ApiEndpointParameterData> parameters;

    private boolean useClient;

    ApiEndpointExecutor(final String name, final ApiEndpointCollection apiCollection, final Method method) throws ApiEndpointParameterException {
        this.name = apiCollection.getName() + "/" + name;
        this.apiCollection = apiCollection;
        this.method = method;
        this.useClient = false;
        this.parameters = this.loadParameters(this.method);
    }

    ApiResponse execute(final ApiClient client, final JsonObject json) throws ApiException {
        try {
            final Object result = ApiEndpointValidator.validateParameters(this.name, this.parameters, json, true);
            if (result == null) return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_A_NUMBER");
            final Object[] parameters = (Object[]) result;
            Object[] methodArgs = null;
            if (this.useClient && client != null) {
                methodArgs = new Object[parameters.length + 1];
                methodArgs[0] = client;
                System.arraycopy(parameters, 0, methodArgs, 1, parameters.length);
            }
            return (ApiResponse) this.method.invoke(this.apiCollection, methodArgs == null ? parameters : methodArgs);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApiException("Unable to execute JsonApi endpoint \"" + this.name + "\".", e);
        }
    }

    private List<ApiEndpointParameterData> loadParameters(final Method method) throws ApiEndpointParameterException {
        final List<ApiEndpointParameterData> parameters = new ArrayList<>();

        for (final Parameter parameter : method.getParameters()) {
            if (!parameter.isAnnotationPresent(ApiParameter.class)) {
                if (!parameter.isAnnotationPresent(ApiParameters.class)) {
                    if (parameter.getType().equals(ApiClient.class)) {
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

    private ApiEndpointParameterData loadParameters(final ApiParameters apiParameters) {
        final List<ApiEndpointParameterData> parameterDataList = new ArrayList<>();
        for (final ApiParameter apiParameter : apiParameters.parameters()) {
            parameterDataList.add(new ApiEndpointParameterData(apiParameter.value(), null, null, apiParameter.optional()));
        }
        return new ApiEndpointParameterData(apiParameters.name(), null, parameterDataList, apiParameters.optional());
    }

    String getName() {
        return this.name;
    }
}
