package net.cryptic_game.backend.base.api;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class ApiCollection {

    private static final Logger logger = LoggerFactory.getLogger(ApiCollection.class);

    private final String name;

    public ApiCollection(String name) {
        this.name = name;
    }

    public ApiCollection() {
        this(null);
    }

    List<ApiEndpointExecutor> load(Class<? extends ApiEndpointExecutor> executorClass) {
        final List<ApiEndpointExecutor> executors = new ArrayList<>();
        for (final Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ApiEndpoint.class)) {
                final String name = method.getAnnotation(ApiEndpoint.class).value();
                if (this.validateMethod(name, method)) {
                    try {
                        executors.add(executorClass.getDeclaredConstructor(String.class, ApiCollection.class, Method.class).newInstance(name, this, method));
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        logger.error("Error while loading Api entrypoint.", e);
                    }
                }
            }
        }
        if (executors.size() == 0) logger.warn("Api Collection \"" + this.name + "\" has no endpoints.");
        return executors;
    }

    private boolean validateMethod(final String name, final Method method) {
        if (method.getReturnType() != JsonObject.class) {
            logger.warn("Endpoint \"" + this.name + "/" + name + "\" has not the return type \"" + JsonObject.class.getName() + "\".");
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }
}
