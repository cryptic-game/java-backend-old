package net.cryptic_game.backend.base.json;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.json.types.InstantTypeAdapter;
import net.cryptic_game.backend.base.json.types.OffsetDateTimeAdapter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.function.Function;

@Slf4j
public final class JsonUtils {

    public static final JsonObject EMPTY_OBJECT = new JsonObject();

    @Getter
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .setExclusionStrategies(new JsonExclusionStrategy())
            .registerTypeHierarchyAdapter(JsonSerializable.class, new JsonSerializableSerializer())
            .registerTypeHierarchyAdapter(Instant.class, new InstantTypeAdapter())
            .registerTypeHierarchyAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

    private JsonUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T fromJson(final JsonElement jsonElement, final Class<T> type) throws JsonParseException {
        return GSON.fromJson(jsonElement, type);
    }

    public static JsonElement toJson(final Object object) throws JsonParseException {
        if (object instanceof JsonElement) {
            return (JsonElement) object;
        }
        return GSON.toJsonTree(object);
    }

    public static <T> JsonArray toArray(final Collection<T> collection, final Function<T, Object> function) {
        final JsonArray array = new JsonArray();
        collection.forEach(item -> array.add(JsonUtils.toJson(function.apply(item))));
        return array;
    }

    public static <C extends Collection<T>, T> C fromArray(final JsonArray array, final C collection, final Class<T> type) {
        return JsonUtils.fromArray(array, collection, element -> JsonUtils.fromJson(element, type));
    }

    public static <C extends Collection<T>, T> C fromArray(final JsonArray array, final C collection, final Function<JsonElement, T> function) {
        array.forEach(item -> collection.add(function.apply(item)));
        return collection;
    }
}
