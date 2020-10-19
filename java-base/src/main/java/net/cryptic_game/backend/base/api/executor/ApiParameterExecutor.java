package net.cryptic_game.backend.base.api.executor;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.LinkedList;
import java.util.List;

@Slf4j
final class ApiParameterExecutor {

    private static final Object[] EMPTY_PARAMETERS = new Object[0];

    private ApiParameterExecutor() {
        throw new UnsupportedOperationException();
    }

    static Object[] parseParameters(final ApiRequest request, final ApiParameterData[] parameters) throws ApiParameterException {
        if (parameters.length == 0) return EMPTY_PARAMETERS;

        final List<Object> values = new LinkedList<>();
        for (ApiParameterData parameter : parameters) {
            final Object value = getParameter(request, parameter);
            if (parameter.getType().equals(ApiParameterType.DAEMON_PARAMETER)) continue;
            values.add(value);
        }
        return values.toArray();
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
        } catch (JsonParseException e) {
            if (log.isInfoEnabled()) {
                log.info("Unable to parse parameter \"{}\" in endpoint \"{}\": {}", parameter.getId(), request.getEndpoint(), e.getMessage(), e);
            }
            throw new ApiParameterException(String.format("INVALID_PARAMETER_%s", parameter.getId()));
        }
    }
}
