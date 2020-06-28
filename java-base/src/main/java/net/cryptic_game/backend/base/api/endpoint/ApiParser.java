package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.json.JsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ApiParser {

    private static final Logger LOG = LoggerFactory.getLogger(ApiParser.class);

    private ApiParser() {
        throw new UnsupportedOperationException();
    }

    public static ApiEndpointCollectionData parseEndpointCollection(final ApiEndpointCollection collection) {
        return new ApiEndpointCollectionData(
                collection.getName(),
                collection.getDescription(),
                collection,
                ApiParser.parse(collection.getName(), collection.getClass(), collection)
        );
    }

    private static Map<String, ApiEndpointData> parse(final String baseName, final Class<?> clazz, final Object object) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .map(method -> parseMethod(baseName, method, object))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ApiEndpointData::getName, Function.identity()));
    }

    private static ApiEndpointData parseMethod(final String baseName, final Method method, final Object object) {
        method.setAccessible(true);
        if (method.isAnnotationPresent(ApiEndpoint.class)) {
            final ApiEndpoint apiEndpoint = method.getAnnotation(ApiEndpoint.class);
            if (!method.getReturnType().equals(ApiResponse.class)) {
                LOG.error("Api Endpoint \"{}\" does not have \"{}\" as return type.", baseName + "/" + apiEndpoint.value(), ApiResponse.class.getName());
                return null;
            } else return new ApiEndpointData(
                    baseName + "/" + apiEndpoint.value(),
                    apiEndpoint.description(),
                    method,
                    object,
                    parseParameters(method.getParameters())
            );
        } else return null;
    }

    public static List<ApiParameterData> parseParameters(final Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(ApiParser::parseParameter)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static ApiParameterData parseParameter(final Parameter parameter) {
        if (parameter.isAnnotationPresent(ApiParameter.class)) {
            final ApiParameter apiParameter = parameter.getAnnotation(ApiParameter.class);
            return new ApiParameterData(apiParameter.value(), apiParameter.special(), apiParameter.description(), apiParameter.optional(), parameter.getType());
        } else return null;
    }

    public static JsonElement toPlayground(final String address, final Collection<ApiEndpointCollectionData> endpointCollections) {
        return JsonBuilder.create("apiUrl", "ws://" + address + "/")
                .add("endpointCollections", endpointCollections.stream()
                        .peek(collection -> collection.setEndpoints(collection.getEndpoints().entrySet()
                                .stream()
                                .map(entry -> {
                                    final ApiEndpointData endpointData = entry.getValue().copy();
                                    final String[] name = endpointData.getName().split("/");
                                    endpointData.setName(name[name.length - 1]);
                                    entry.setValue(endpointData);
                                    return entry;
                                }).peek(entry -> entry.getValue().setParameters(entry.getValue().getParameters()
                                        .stream()
                                        .filter(parameter -> parameter.getSpecial().equals(ApiParameterSpecialType.NORMAL))
                                        .collect(Collectors.toList())))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                        .collect(Collectors.toList()))
                .build();
    }
}
