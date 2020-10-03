package net.cryptic_game.backend.base.api.parser;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.Group;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
final class ApiEndpointParser {

    private ApiEndpointParser() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    static Map<String, ApiEndpointData> parseEndpoints(@NotNull final Object object, @NotNull final Class<?> clazz, @NotNull final Set<Group> groups) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .map(method -> parseEndpoint(object, clazz, method, groups))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableMap(ApiEndpointData::getId, Function.identity()));
    }

    @Nullable
    private static ApiEndpointData parseEndpoint(@NotNull final Object instance,
                                                 @NotNull final Class<?> clazz,
                                                 @NotNull final Method method,
                                                 @NotNull final Set<Group> groups) {
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

        if (!method.getReturnType().equals(ApiResponse.class) && !method.getReturnType().equals(Mono.class)) {
            log.error("Endpoint {}.{} cannot be parsed because it does not have the return type {} or {}.",
                    clazz.getName(), method.getName(), ApiResponse.class.getName(), Mono.class.getName());
            if (!hasAccess) method.setAccessible(false);
            return null;
        }

        final ApiEndpoint endpointAnnotation = method.getAnnotation(ApiEndpoint.class);
        final List<ApiParameterData> parameters = ApiParameterParser.parseParameters(clazz, method);

        return new ApiEndpointData(endpointAnnotation.id(),
                Arrays.stream(endpointAnnotation.groups())
                        .map(id -> {
                            final Optional<Group> group = groups.parallelStream().filter(g -> g.getId().equals(id)).findAny();
                            if (group.isEmpty()) {
                                log.error("The group with the id {} could not be found.", id);
                                return null;
                            }
                            return group.get();
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()),
                String.join("\n", endpointAnnotation.description()),
                parameters == null ? Collections.emptyList() : parameters,
                parameters != null,
                instance, clazz, method);
    }
}
