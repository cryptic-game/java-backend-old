package net.cryptic_game.backend.base.json.types;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantTypeAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public JsonElement serialize(final Instant src, final Type typeOfSrc, final JsonSerializationContext context) {
        return JsonUtils.toJson(src.toEpochMilli());
    }

    @Override
    public Instant deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        try {
            if (json.isJsonPrimitive()) {
                return Instant.ofEpochMilli(JsonUtils.fromJson(json, long.class));
            } else {
                throw new JsonParseException("Unable to parse a non \"" + JsonPrimitive.class.getName() + "\" into a \"" + Instant.class.getName() + "\".");
            }
        } catch (JsonTypeMappingException e) {
            throw new JsonParseException(e);
        }
    }
}
