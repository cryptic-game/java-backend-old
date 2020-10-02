package net.cryptic_game.backend.base.api.executor;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiContext;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class ApiExecutor {

    private ApiExecutor() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static ApiResponse execute(@NotNull final Map<String, ApiEndpointData> endpoints, @NotNull final ApiRequest request, @Nullable final ApiAuthenticationProvider authenticationProvider) {
        final ApiEndpointData endpoint = endpoints.get(request.getEndpoint());
        if (endpoint == null) return new ApiResponse(ApiResponseStatus.NOT_FOUND, "ENDPOINT");
        if (!endpoint.getGroups().isEmpty() && authenticationProvider != null) {
            if (request.getAuthenticationGroups() == null || request.getAuthenticationGroups().isEmpty()) {
                return new ApiResponse(ApiResponseStatus.UNAUTHORIZED, HttpResponseStatus.UNAUTHORIZED.toString());
            }
            if (!authenticationProvider.isPermitted(endpoint.getGroups(), request.getAuthenticationGroups())) {
                return new ApiResponse(ApiResponseStatus.FORBIDDEN, HttpResponseStatus.FORBIDDEN.toString());
            }
        }
        return ApiEndpointExecutor.execute(new ApiContext(request), endpoint);
    }
}
