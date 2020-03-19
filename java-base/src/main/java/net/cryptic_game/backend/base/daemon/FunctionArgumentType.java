package net.cryptic_game.backend.base.daemon;

import com.google.gson.JsonElement;

public enum FunctionArgumentType {

    STRING(String.class),
    CHARACTER(Character.class, char.class),
    NUMBER(Number.class),
    BOOLEAN(Boolean.class, boolean.class),
    JSON(JsonElement.class);

    final Class<?>[] javaMapping;

    FunctionArgumentType(final Class<?>... javaMapping) {
        this.javaMapping = javaMapping;
    }

    public static FunctionArgumentType getByJavaMapping(final Class<?> javaMapping) {
        for (final FunctionArgumentType value : FunctionArgumentType.values()) {
            for (final Class<?> clazz : value.getJavaMapping()) {
                if (javaMapping.isAssignableFrom(clazz)) {
                    return value;
                }
            }
        }
        return null;
    }

    public Class<?>[] getJavaMapping() {
        return this.javaMapping;
    }
}
