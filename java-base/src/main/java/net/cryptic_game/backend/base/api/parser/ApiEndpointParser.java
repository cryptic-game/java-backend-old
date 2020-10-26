package net.cryptic_game.backend.base.api.parser;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
final class ApiEndpointParser {

    private static final ApiParameterData[] EMPTY_PARAMETERS = new ApiParameterData[0];

    private ApiEndpointParser() {
        throw new UnsupportedOperationException();
    }

    static Map<String, ApiEndpointData> parseEndpoints(final Object object, final Class<?> clazz, final boolean disabled, final ApiAuthenticator authenticator) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .parallel()
                .map(method -> parseEndpoint(object, clazz, method, disabled, authenticator))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableMap(ApiEndpointData::getId, Function.identity()));
    }

    private static ApiEndpointData parseEndpoint(final Object instance, final Class<?> clazz, final Method method, final boolean disabled, final ApiAuthenticator authenticator) {
        final boolean hasAccess;
        try {
            hasAccess = method.canAccess(instance);
        } catch (IllegalArgumentException e) {
            return null;
        }

        if (!hasAccess && !method.trySetAccessible()) return null;

        if (!method.isAnnotationPresent(ApiEndpoint.class)) {
            if (!hasAccess) method.setAccessible(false);
            return null;
        }

        final Class<?> returnType = method.getReturnType();
        if (!(returnType.equals(ApiResponse.class) || returnType.equals(Mono.class))) {
            log.error("Endpoint {}.{} cannot be parsed because it does not have the return type {} or {}<{}>.",
                    clazz.getName(), method.getName(), ApiResponse.class.getName(), Mono.class.getName(), ApiResponse.class.getName());
            if (!hasAccess) method.setAccessible(false);
            return null;
        }

        final ApiEndpoint endpointAnnotation = method.getAnnotation(ApiEndpoint.class);
        final ApiParameterData[] parameters = ApiParameterParser.parseParameters(clazz, method);

        return new ApiEndpointData(
                String.join("\n", endpointAnnotation.description()),
                endpointAnnotation.authentication(),
                clazz,
                endpointAnnotation.disabled() || parameters == null || disabled,
                authenticator,
                endpointAnnotation.id(),
                parameters == null || parameters.length == 0 ? EMPTY_PARAMETERS : parameters,
                instance,
                method
        );
    }
}
