package net.cryptic_game.backend.base.api.parser;

import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterData;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Slf4j
final class ApiParameterParser {

    private ApiParameterParser() {
        throw new UnsupportedOperationException();
    }

    static ApiParameterData[] parseParameters(final Class<?> clazz, final Method method) {
        final Parameter[] methodParameters = method.getParameters();
        final ApiParameterData[] parameterData = new ApiParameterData[methodParameters.length];

        for (int i = 0; i < methodParameters.length; i++) {
            final ApiParameterData currentParameterData = parseParameter(methodParameters[i]);
            if (currentParameterData == null) {
                log.error("Parameter {}.{}.{} is missing Annotation {}.",
                        clazz.getName(), method.getName(), methodParameters[i].getName(), ApiParameter.class.getName());
                return null;
            } else parameterData[i] = currentParameterData;
        }

        return parameterData;
    }

    private static ApiParameterData parseParameter(final Parameter parameter) {
        if (!parameter.isAnnotationPresent(ApiParameter.class)) return null;
        final ApiParameter parameterAnnotation = parameter.getAnnotation(ApiParameter.class);

        return new ApiParameterData(
                parameterAnnotation.id(),
                parameterAnnotation.required(),
                String.join("\n", parameterAnnotation.description()),
                parameter.getType(),
                parameterAnnotation.type()
        );
    }
}
