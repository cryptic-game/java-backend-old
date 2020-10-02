package net.cryptic_game.backend.base.api.executor;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiContext;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.api.exception.ApiInternalParameterException;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@Slf4j
final class ApiEndpointExecutor {

    private ApiEndpointExecutor() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    static ApiResponse execute(@NotNull final ApiContext context, @NotNull final ApiEndpointData endpoint) {
        if (!endpoint.isEnabled()) return new ApiResponse(ApiResponseStatus.SERVICE_UNAVAILABLE, "ENDPOINT_DISABLED");

        Object[] parameters;

        try {
            parameters = ApiParameterExecutor.parseParameters(context, endpoint.getParameters());
        } catch (ApiInternalParameterException e) {
            log.error("Unable to parse parameters.", e);
            return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
        } catch (ApiParameterException e) {
            if (log.isDebugEnabled()) log.debug("Unable to parse parameters.", e);
            return new ApiResponse(ApiResponseStatus.BAD_REQUEST, e.getMessage());
        }

        try {
            final ApiResponse response = (ApiResponse) endpoint.getMethod().invoke(endpoint.getInstance(), parameters);

            if (response == null) {
                log.error("Endpoint {}.{} returned null, which is not allowed.", endpoint.getClazz().getName(), endpoint.getMethod().getName());
                return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
            }

            return response;
        } catch (IllegalArgumentException e) {
            log.error("Endpoint {}.{} does not has the right parameters.", endpoint.getClazz().getName(), endpoint.getMethod().getName(), e);
        } catch (InvocationTargetException e) {
            log.error("An exception was thrown in endpoint {}.{}.", endpoint.getClazz().getName(), endpoint.getMethod().getName(), e.getTargetException());
        } catch (Throwable e) {
            log.error("Unable to execute endpoint {}.{}.", endpoint.getClazz().getName(), endpoint.getMethod().getName(), e);
        }

        return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
    }
}
