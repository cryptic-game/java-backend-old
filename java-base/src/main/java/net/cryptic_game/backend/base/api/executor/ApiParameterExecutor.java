package net.cryptic_game.backend.base.api.executor;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSyntaxException;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.exception.ApiInternalParameterException;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Arrays;
import java.util.Objects;

final class ApiParameterExecutor {

    private static final Object[] EMPTY_PARAMETERS = new Object[0];

    private ApiParameterExecutor() {
        throw new UnsupportedOperationException();
    }

    static Object[] parseParameters(final ApiRequest request, final ApiParameterData[] parameters) throws ApiParameterException {
        if (parameters.length == 0) return EMPTY_PARAMETERS;

        final Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            values[i] = getParameter(request, parameters[i]);
        }

        return Arrays.stream(values).filter(Objects::nonNull).toArray();
    }

    private static Object getParameter(final ApiRequest request, final ApiParameterData parameter) throws ApiParameterException {
        switch (parameter.getType()) {
            case USER:
            case NORMAL:
                return parseNormalParameter(request, parameter);
            case REQUEST:
                return request;
            case DAEMON_PARAMETER:
                final JsonElement jsonValue = request.getData().get(parameter.getId());
                if (parameter.isRequired() && (jsonValue == null || jsonValue instanceof JsonNull)) {
                    throw new ApiParameterException(String.format("PARAMETER_%s_MISSING", parameter.getId()));
                }
                return null;
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Object parseNormalParameter(final ApiRequest request, final ApiParameterData parameter) throws ApiParameterException {
        final JsonElement jsonValue = request.getData().get(parameter.getId());
        if (parameter.isRequired() && (jsonValue == null || jsonValue instanceof JsonNull)) {
            throw new ApiParameterException(String.format("PARAMETER_%s_MISSING", parameter.getId()));
        }

        try {
            return JsonUtils.fromJson(jsonValue, parameter.getClassType());
        } catch (JsonSyntaxException e) {
            throw new ApiInternalParameterException(String.format("Unable to parse parameter \"%s\" in endpoint \"%s\": %s",
                    parameter.getId(), request.getEndpoint(), e.getMessage()), e);
        }
    }
}
