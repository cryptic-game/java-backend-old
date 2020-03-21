package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;

public class ApiResponse {

    private final ApiResponseType type;
    private final JsonElement data;
    private final String message;

    public ApiResponse(final ApiResponseType type) {
        this(type, null, (JsonElement) null);
    }

    public ApiResponse(final ApiResponseType type, final String message) {
        this(type, message, (JsonElement) null);
    }

    public ApiResponse(final ApiResponseType type, final JsonElement data) {
        this(type, null, data);
    }

    public ApiResponse(final ApiResponseType type, final JsonSerializable data) {
        this(type, null, data);
    }

    public ApiResponse(final ApiResponseType type, final String message, final JsonSerializable data) {
        this(type, message, data.serialize());
    }

    public ApiResponse(final ApiResponseType type, final String message, final JsonElement data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public ApiResponseType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean hasErrorMessage() {
        return this.message != null;
    }

    public JsonElement getData() {
        return this.data;
    }
}
