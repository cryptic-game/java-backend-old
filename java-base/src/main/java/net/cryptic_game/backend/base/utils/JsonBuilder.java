package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.UUID;

public class JsonBuilder implements JsonSerializable {

    private final JsonObject json;

    private JsonBuilder() {
        this.json = new JsonObject();
    }

    /**
     * Creates a new object of {@link JsonBuilder}.
     *
     * @return the {@link JsonBuilder} that has been created.
     */
    public static JsonBuilder anJSON() {
        return new JsonBuilder();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property
     * with an associated number value.
     *
     * @param key   name of the property.
     * @param value the number value associated with the property.
     * @return the a newly created object of {@link JsonObject}.
     */
    public static JsonObject simple(final String key, final Number value) {
        return anJSON().add(key, value).build();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property
     * with an associated string value.
     *
     * @param key   name of the property.
     * @param value the string value associated with the property.
     * @return the newly created object of {@link JsonObject}.
     */
    public static JsonObject simple(final String key, final String value) {
        return anJSON().add(key, value).build();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property
     * with an associated boolean value.
     *
     * @param key   name of the property.
     * @param value the boolean value associated with the property.
     * @return the newly created object of {@link JsonObject}.
     */
    public static JsonObject simple(final String key, final Boolean value) {
        return anJSON().add(key, value).build();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property
     * with an associated character value.
     *
     * @param key   name of the property.
     * @param value the character value associated with the property.
     * @return the newly created object of {@link JsonObject}.
     */
    public static JsonObject simple(final String key, final Character value) {
        return anJSON().add(key, value).build();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property
     * with an associated {@link JsonElement} value.
     *
     * @param key   name of the property.
     * @param value the {@link JsonElement} value associated with the property.
     * @return the newly created object of {@link JsonObject}.
     */
    public static JsonObject simple(final String key, final JsonElement value) {
        return anJSON().add(key, value).build();
    }

    /**
     * Convenience method to create a new {@link JsonObject}. The newly created object contains one property,
     * named "error" with an associated string value.
     *
     * @param message the string value associated with the property.
     * @return the newly created object of {@link JsonObject}.
     */
    public static JsonObject error(final String message) {
        return simple("error", message);
    }

    /**
     * @deprecated use {@link JsonBuilder#build()}
     */
    @Deprecated
    @Override
    public JsonObject serialize() {
        return this.build();
    }

    /**
     * Convenience method to add a new property associated with a number value.
     *
     * @param key   name of the property.
     * @param value number value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final Number value) {
        this.json.addProperty(key, value);
        return this;
    }

    /**
     * Convenience method to add a new property associated with a string value.
     *
     * @param key   name of the property.
     * @param value string value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final String value) {
        this.json.addProperty(key, value);
        return this;
    }

    /**
     * Convenience method to add a new property associated with a boolean value.
     *
     * @param key   name of the property.
     * @param value boolean value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final Boolean value) {
        this.json.addProperty(key, value);
        return this;
    }

    /**
     * Convenience method to add a new property associated with a character value.
     *
     * @param key   name of the property.
     * @param value character value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final Character value) {
        this.json.addProperty(key, value);
        return this;
    }

    /**
     * Convenience method to add a new property associated with a {@link JsonElement} value.
     *
     * @param key   name of the property.
     * @param value {@link JsonElement} value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final JsonElement value) {
        this.json.add(key, value);
        return this;
    }

    /**
     * Convenience method to add a new property associated with a {@link UUID} value.
     *
     * @param key   name of the property.
     * @param value {@link UUID} value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     */
    public JsonBuilder add(final String key, final UUID value) {
        this.json.addProperty(key, value.toString());
        return this;
    }

    /**
     * Convenience method to add a new property associated with a {@link List} value.
     * List can have {@link String}, {@link Number}, {@link Boolean} or {@link Character} values.
     *
     * @param key    name of the property.
     * @param values {@link List} value of associated with the property.
     * @return the current object of {@link JsonBuilder}.
     * @throws InvalidPropertiesFormatException Type is not supported by {@link JsonArray}
     */
    public <T> JsonBuilder add(final String key, final List<T> values) throws InvalidPropertiesFormatException {

        JsonArray array = new JsonArray();

        for (T value : values) {
            if (value instanceof String) {
                array.add((String) value);
            } else if (value instanceof Number) {
                array.add((Number) values);
            } else if (value instanceof Boolean) {
                array.add((Boolean) value);
            } else if (value instanceof Character) {
                array.add((Character) value);
            } else if (value instanceof JsonElement) {
                array.add((JsonElement) value);
            } else if (value instanceof LocalDateTime) {
                array.add(((LocalDateTime) value).toInstant(ZoneOffset.UTC).toEpochMilli());
            } else if (value instanceof LocalDate) {
                array.add(((LocalDate) value).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
            } else if (value instanceof JsonSerializable) {
                array.add(((JsonSerializable) value).serialize());
            } else {
                throw new InvalidPropertiesFormatException(value.getClass() + " can not be added to a JsonArray!");
            }
        }

        this.json.add(key, array);

        return this;
    }

    /**
     * Creates a {@link JsonObject} containing all previously to the object of {@link JsonBuilder} added properties.
     *
     * @return the {@link JsonObject} containing all previously added properties.
     */
    public JsonObject build() {
        return this.json;
    }
}
