package net.cryptic_game.backend.base.api.executor;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.exception.ApiInternalParameterException;
import net.cryptic_game.backend.base.api.exception.ApiParameterException;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Slf4j
final class ApiEndpointExecutor {

    private ApiEndpointExecutor() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings({"unchecked"})
    static Mono<ApiResponse> execute(final ApiRequest request, final ApiEndpointData endpoint) {
        if (!endpoint.isEnabled()) {
            return Mono.just(new ApiResponse(HttpResponseStatus.SERVICE_UNAVAILABLE, "ENDPOINT_DISABLED"));
        }

        if (!endpoint.getAuthenticator().isPermitted(request, endpoint.getAuthentication(), endpoint)) {
            return Mono.just(new ApiResponse(HttpResponseStatus.UNAUTHORIZED));
        }

        final Object[] parameters;

        try {
            parameters = ApiParameterExecutor.parseParameters(request, endpoint.getParameters());
        } catch (ApiInternalParameterException e) {
            log.error("Unable to parse parameters.", e);
            return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        } catch (ApiParameterException e) {
            return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, e.getMessage()));
        } catch (Throwable e) {
            log.error("Unable to parse parameters of endpoint {}.", getMethodPath(endpoint), e);
            return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }

        try {
            final Object response = endpoint.getMethod().invoke(endpoint.getInstance(), parameters);

            if (response == null) {
                log.error("Endpoint {} returned null, which is not allowed.", getMethodPath(endpoint));
                return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
            }

            if (response instanceof ApiResponse) {
                return Mono.just((ApiResponse) response);
            } else {
                return (Mono<ApiResponse>) response;
            }

        } catch (IllegalArgumentException e) {
            log.error("Endpoint {} does not has the right parameters.", getMethodPath(endpoint), e);
        } catch (InvocationTargetException e) {
            log.error("An exception was thrown in endpoint {}.", getMethodPath(endpoint), e.getTargetException());
        } catch (Throwable e) {
            log.error("Unable to execute endpoint {}.", getMethodPath(endpoint), e);
        }

        return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
    }

    private static String getMethodPath(final ApiEndpointData endpoint) {
        return String.format("%s.%s", endpoint.getClazz().getName(), endpoint.getMethod().getName());
    }
}
