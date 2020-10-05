package net.cryptic_game.backend.base.api.executor;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.cryptic_game.backend.base.api.data.ApiContext;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

final class ApiParameterExecutor {

    private ApiParameterExecutor() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    static Object[] parseParameters(@NotNull final ApiContext context, @NotNull final Collection<ApiParameterData> parameters) throws ApiParameterException {
        final Object[] values = new Object[parameters.size()];

        int i = 0;
        for (final ApiParameterData parameter : parameters) {
            values[i] = getParameter(context, parameter);
            i++;
        }

        return values;
    }

    @Nullable
    private static Object getParameter(@NotNull final ApiContext context, @NotNull final ApiParameterData parameter) throws ApiParameterException {
        switch (parameter.getType()) {
            case NORMAL:
            case USER:
                final JsonElement jsonValue = context.getRequest().getData().get(parameter.getId());
                if (parameter.isRequired() && (jsonValue == null || jsonValue instanceof JsonNull)) {
                    throw new ApiParameterException("The parameter \"" + parameter.getId() + "\" was not found.");
                }

                try {
                    return JsonUtils.fromJson(jsonValue, parameter.getClassType());
                } catch (JsonTypeMappingException e) {
                    throw new ApiParameterException("Unable to parse parameter \"" + parameter.getId() + "\"", e);
                }
            case ENDPOINT:
                return context.getRequest().getEndpoint();
            case TAG:
                return context.getRequest().getTag();
            case DATA:
                return context.getRequest().getData();
            default:
                throw new IllegalArgumentException();
        }
    }
}
