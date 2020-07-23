package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.json.JsonUtils;

@EqualsAndHashCode
@Getter
public final class ApiResponse implements JsonSerializable {

    private final ApiResponseType responseType;
    private final String message;
    private final JsonElement data;
    @Setter
    private String tag;

    public ApiResponse(final ApiResponseType responseCode) {
        this(responseCode, null, null);
    }

    public ApiResponse(final ApiResponseType responseCode, final String message) {
        this(responseCode, message, null);
    }

    public ApiResponse(final ApiResponseType responseCode, final Object data) {
        this(responseCode, null, data);
    }

    public ApiResponse(final ApiResponseType responseType, final String message, final Object data) {
        this(responseType, message, JsonUtils.toJson(data));
    }

    public ApiResponse(final ApiResponseType responseType, final String message, final JsonElement data) {
        this.responseType = responseType;
        this.message = message;
        this.data = data;
    }

    @Override
    public JsonElement serialize() {
        return this.serialize(false);
    }

    public JsonElement serialize(final boolean small) {
        final JsonBuilder info = small
                ? JsonBuilder.create("error", this.responseType.isError())
                .add("message", this.message != null, () -> this.message)
                : JsonBuilder.create("tag", this.getTag())
                .add("status", this.getResponseType())
                .add("message", this.message != null, this::getMessage);

        return JsonBuilder.create("info", info)
                .add("data", !(this.data instanceof JsonNull), this::getData)
                .build();
    }
}
