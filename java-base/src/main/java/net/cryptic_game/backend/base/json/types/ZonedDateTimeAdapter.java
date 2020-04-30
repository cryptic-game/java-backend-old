package net.cryptic_game.backend.base.json.types;

import com.google.gson.*;
import net.cryptic_game.backend.base.json.JsonTypeMappingException;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {

    @Override
    public JsonElement serialize(final ZonedDateTime src, final Type typeOfSrc, final JsonSerializationContext context) {
        return JsonUtils.toJson(src.toInstant());
    }

    @Override
    public ZonedDateTime deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        try {
            if (json.isJsonPrimitive()) {
                return ZonedDateTime.ofInstant(JsonUtils.fromJson(json, Instant.class), ZoneOffset.UTC);
            } else {
                throw new JsonParseException("Unable to parse a non \"" + JsonPrimitive.class.getName() + "\" into a \"" + LocalDate.class.getName() + "\".");
            }
        } catch (JsonTypeMappingException e) {
            throw new JsonParseException(e);
        }
    }
}
