package net.cryptic_game.backend.base.type;

import java.util.HashMap;
import java.util.Map;

public final class TypeMappingController {

    private final Map<Class<?>, TypeMapping<?>> mappings;

    public TypeMappingController() {
        this.mappings = new HashMap<>();
        this.registerTypeMappings();
    }

    private void registerTypeMappings() {
        this.addTypeMapping(new BooleanMapping(), boolean.class);
        this.addTypeMapping(new ByteMapping(), byte.class);
        this.addTypeMapping(new DoubleMapping(), double.class);
        this.addTypeMapping(new FloatMapping(), float.class);
        this.addTypeMapping(new IntegerMapping(), int.class);
        this.addTypeMapping(new LongMapping(), long.class);
        this.addTypeMapping(new ShortMapping(), short.class);
        this.addTypeMapping(new StringMapping());
        this.addTypeMapping(new LevelType());
    }

    public void addTypeMapping(final TypeMapping<?> mapping, final Class<?>... optionalTypes) {
        this.mappings.put(mapping.getType(), mapping);
        for (final Class<?> optionalType : optionalTypes) this.mappings.putIfAbsent(optionalType, mapping);
    }

    private <T> TypeMapping<T> getMapping(final Class<T> type) {
        final TypeMapping<?> typeMapping = this.mappings.get(type);
        if (typeMapping == null) return null;
        return (TypeMapping<T>) typeMapping;
    }

    public <T> String toString(final T object) throws TypeMappingException {
        if (object == null) return "";
        final Class<T> type = (Class<T>) object.getClass();
        final TypeMapping<T> typeMapping = this.getMapping(type);
        if (typeMapping == null)
            throw new TypeMappingException("Cannot find Type Mapping for Type \"" + type.getName() + "\".");
        try {
            return typeMapping.toString(object);
        } catch (Exception e) {
            throw new TypeMappingException("Error while converting a \"" + type.getName() + "\" into a \"" + String.class.getName() + "\".", e);
        }
    }

    public <T> T fromString(final Class<T> type, final String string) throws TypeMappingException {
        final TypeMapping<T> typeMapping = this.getMapping(type);
        if (typeMapping == null)
            throw new TypeMappingException("Cannot find Type Mapping for Type \"" + type.getName() + "\".");
        try {
            return typeMapping.fromString(string);
        } catch (Exception e) {
            throw new TypeMappingException("Error while converting a \"" + String.class.getName() + "\" into a \"" + type.getName() + "\".", e);
        }
    }
}
