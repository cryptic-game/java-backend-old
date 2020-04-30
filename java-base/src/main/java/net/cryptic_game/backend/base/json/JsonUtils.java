package net.cryptic_game.backend.base.json;

import com.google.gson.*;
import net.cryptic_game.backend.base.json.types.InstantTypeAdapter;
import net.cryptic_game.backend.base.json.types.ZonedDateTimeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.function.Function;

public final class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .setExclusionStrategies(new JsonExclusionStrategy())
            .registerTypeHierarchyAdapter(JsonSerializable.class, new JsonSerializableSerializer())
            .registerTypeHierarchyAdapter(Instant.class, new InstantTypeAdapter())
            .registerTypeHierarchyAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();

    private JsonUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T fromJson(final JsonElement jsonElement, final Class<T> type) throws JsonTypeMappingException {
        if (type.isAssignableFrom(LocalDateTime.class) || type.isAssignableFrom(LocalDate.class)) {
            log.warn("Use \"{}\" instant of \"{}\".", ZonedDateTime.class.getName(), LocalDateTime.class.getName());
        }
        try {
            return gson.fromJson(jsonElement, type);
        } catch (Exception e) {
            throw new JsonTypeMappingException("Error while converting a \"" + JsonElement.class.getName() + "\" into a \"" + type.getName() + "\".", e);
        }
    }

    public static JsonElement toJson(final Object object) throws JsonTypeMappingException {
        if (object instanceof JsonElement) {
            return (JsonElement) object;
        }
        if (object instanceof LocalDateTime || object instanceof LocalDate) {
            log.warn("Use \"{}\" instant of \"{}\".", ZonedDateTime.class.getName(), LocalDateTime.class.getName());
        }
        try {
            return gson.toJsonTree(object);
        } catch (Exception e) {
            throw new JsonTypeMappingException("Error while converting a \"" + object.getClass().getName() + "\" into a \"" + JsonElement.class.getName() + "\".", e);
        }
    }

    public static <T> JsonArray toArray(final Collection<T> collection, final Function<T, Object> function) {
        final JsonArray array = new JsonArray();
        collection.forEach(item -> array.add(JsonUtils.toJson(function.apply(item))));
        return array;
    }

    public static Gson getGson() {
        return gson;
    }
}
