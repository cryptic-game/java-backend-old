package net.cryptic_game.backend.base.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class JsonSerializableSerializer implements JsonSerializer<JsonSerializable> {

    @Override
    public JsonElement serialize(final JsonSerializable src, final Type typeOfSrc, final JsonSerializationContext context) {
        return src.serialize();
    }
}
