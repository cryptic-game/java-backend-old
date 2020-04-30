package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Objects;

public class ApiResponse {

    private final ApiResponseType type;
    private final String message;
    private final Object data;

    public ApiResponse(final ApiResponseType type) {
        this(type, null, null);
    }

    public ApiResponse(final ApiResponseType type, final String message) {
        this(type, message, null);
    }

    public ApiResponse(final ApiResponseType type, final Object data) {
        this(type, null, data);
    }

    public ApiResponse(final ApiResponseType type, final String message, final Object data) {
        this.type = type;
        this.message = message;
        this.data = data;
    }

    public boolean hasMessage() {
        return this.message != null;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public ApiResponseType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    public JsonElement getData() {
        return JsonUtils.toJson(this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiResponse)) return false;
        ApiResponse that = (ApiResponse) o;
        return this.getType() == that.getType() &&
                this.message.equals(that.message) &&
                this.getData().equals(that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getType(), this.message, this.getData());
    }
}
