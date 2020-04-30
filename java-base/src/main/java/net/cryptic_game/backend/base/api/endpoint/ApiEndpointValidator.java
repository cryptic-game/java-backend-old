package net.cryptic_game.backend.base.api.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

final class ApiEndpointValidator {

    private static final Logger log = LoggerFactory.getLogger(ApiEndpointValidator.class);

    private ApiEndpointValidator() {
    }

    static boolean validateMethod(final ApiEndpointCollection apiCollection, final String name, final Method method) {
        if (method.getReturnType() != ApiResponse.class) {
            log.warn("Api \"" + apiCollection.getName() + "/" + name + "\" has not the return type \"" + ApiResponse.class.getName() + "\".");
            return false;
        }
        return true;
    }
}
