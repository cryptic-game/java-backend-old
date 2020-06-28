package net.cryptic_game.backend.base.type;

public final class TypeUtils {

    private static final TypeMappingController TYPE_MAPPING_CONTROLLER = new TypeMappingController();

    private TypeUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T fromString(final Class<T> type, final String string) throws TypeMappingException {
        return TYPE_MAPPING_CONTROLLER.fromString(type, string);
    }

    public static String toString(final Object object) throws TypeMappingException {
        return TYPE_MAPPING_CONTROLLER.toString(object);
    }

    public static void addTypeMapping(final TypeMapping<?> typeMapping) {
        TYPE_MAPPING_CONTROLLER.addTypeMapping(typeMapping);
    }
}
