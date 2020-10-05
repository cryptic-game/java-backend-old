package net.cryptic_game.backend.base.api.executor;

import net.cryptic_game.backend.base.api.data.ApiAuthenticationProvider;
import net.cryptic_game.backend.base.api.data.ApiContext;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.util.Map;

public final class ApiExecutor {

    private ApiExecutor() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Mono<ApiResponse> execute(@NotNull final Map<String, ApiEndpointData> endpoints, @NotNull final ApiRequest request, @Nullable final ApiAuthenticationProvider authenticationProvider) {
        final ApiEndpointData endpoint = endpoints.get(request.getEndpoint());
        if (endpoint == null) return Mono.just(new ApiResponse(ApiResponseStatus.NOT_FOUND, "ENDPOINT"));
        if (authenticationProvider != null) {
            if (authenticationProvider.usesGroups()) {
                if (!endpoint.getGroups().isEmpty()) {
                    if (request.getAuthenticationGroups() == null || request.getAuthenticationGroups().isEmpty()) {
                        return Mono.just(new ApiResponse(ApiResponseStatus.UNAUTHORIZED));
                    }
                    if (!authenticationProvider.isPermitted(endpoint.getGroups(), request.getAuthenticationGroups())) {
                        return Mono.just(new ApiResponse(ApiResponseStatus.FORBIDDEN));
                    }
                }
            } else {
                if (!authenticationProvider.isPermitted(endpoint.getGroups(), request.getAuthenticationGroups())) {
                    return Mono.just(new ApiResponse(ApiResponseStatus.UNAUTHORIZED));
                }
            }
        }
        return ApiEndpointExecutor.execute(new ApiContext(request), endpoint);
    }
}
