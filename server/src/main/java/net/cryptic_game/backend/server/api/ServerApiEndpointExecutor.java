package net.cryptic_game.backend.server.api;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.*;
import net.cryptic_game.backend.server.client.Client;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ServerApiEndpointExecutor extends ApiEndpointExecutor {

    public ServerApiEndpointExecutor(String name, ApiCollection apiCollection, Method method) throws ApiParameterException {
        super(name, apiCollection, method);
    }

    @Override
    protected List<ApiParameterData> loadParameters(Method method) throws ApiParameterException {
        final List<ApiParameterData> parameters = new ArrayList<>();

        if (method.getParameters()[0].getType() != Client.class)
            throw new ApiParameterException("First parameter \"" + method.getParameters()[0].getName() + "\" of endpoint \"" + this.name + "\" is not \"" + Client.class.getName() + "\" type.");

        for (int i = 1; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (!parameter.isAnnotationPresent(ApiParameter.class)) {
                throw new ApiParameterException("Parameter \"" + parameter.getName() + "\" of endpoint \"" + this.name + "\" does not have \"" + ApiParameter.class.getName() + "\" annotation.");
            }
            final ApiParameter apiParameter = parameter.getAnnotation(ApiParameter.class);
            parameters.add(new ApiParameterData(apiParameter.value().toLowerCase(), parameter.getType(), apiParameter.optional()));
        }

        return parameters;
    }

    @Override
    public JsonObject execute(ApiExecutionData data) throws ApiException {
        if (!(data instanceof ServerApiExecutionData)) throw new ApiException("Provided " + data.getClass().toString()
                + " is not instance of \"" + ServerApiExecutionData.class.toString() + "\".");

        Object[] validParameters = this.validateParameters(((ServerApiExecutionData) data).getData());
        Object[] parameters= new Object[validParameters.length + 1];
        parameters[0] = ((ServerApiExecutionData) data).getClient();
        System.arraycopy(validParameters, 0, parameters, 1, validParameters.length);

        try {
            return (JsonObject) this.method.invoke(this.apiCollection, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApiException("Unable to execute JsonApi entrypoint \"" + this.name + "\".", e);
        }
    }
}
