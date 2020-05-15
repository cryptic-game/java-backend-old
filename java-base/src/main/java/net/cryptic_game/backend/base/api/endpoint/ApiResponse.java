package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Objects;

public class ApiResponse {

    private final ApiResponseType type;
    private final String message;
    private final Object data;

    public ApiResponse(ApiResponseType type) {
        this(type, null, null);
    }

    public ApiResponse(ApiResponseType type, String message) {
        this(type, message, null);
    }

    public ApiResponse(ApiResponseType type, Object data) {
        this(type, null, data);
    }

    public ApiResponse(ApiResponseType type, String message, Object data) {
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

    public JsonElement getData() {
        return JsonUtils.toJson(this.data);
    }

    public ApiResponseType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiResponse)) return false;
        ApiResponse that = (ApiResponse) o;
        return getType() == that.getType() &&
                Objects.equals(getMessage(), that.getMessage()) &&
                Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getMessage(), getData());
    }

    public String toString() {
        return "ApiResponse(type=" + this.getType() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }
}
