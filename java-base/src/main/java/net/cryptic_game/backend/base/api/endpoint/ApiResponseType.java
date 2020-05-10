package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonSerializable;

public enum ApiResponseType implements JsonSerializable {

    // Information responses

    // Successful responses
    OK(200, "OK", false),

    // Redirection messages

    // Client error responses
    BAD_REQUEST(400, "BAD_REQUEST", true),
    UNAUTHORIZED(401, "UNAUTHORIZED", true),
    FORBIDDEN(403, "FORBIDDEN", true),
    NOT_FOUND(404, "NOT_FOUND", true),
    IM_A_TEAPOT(418, "IM_A_TEAPOT", true),

    // Server error responses
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", true),
    NOT_IMPLEMENTED(501, "NOT_IMPLEMENTED", true),
    BAD_GATEWAY(502, "BAD_GATEWAY", true),

    // Custom
    PUSH(900, "PUSH", false),
    ALREADY_EXISTS(901, "ALREADY_EXISTS", true);

    final int code;
    final String name;
    final boolean error;

    ApiResponseType(final int code, final String name, final boolean error) {
        this.code = code;
        this.name = name;
        this.error = error;
    }

    @Override
    public JsonObject serialize() {
        return this.serialize(false);
    }

    public JsonObject serialize(final boolean showNotification) {
        final JsonObject json = new JsonObject();
        if (showNotification) json.addProperty("notification", false);
        json.addProperty("code", this.code);
        json.addProperty("name", this.name);
        json.addProperty("error", this.error);
        return json;
    }

    @Override
    public String toString() {
        return this.code + " " + this.name;
    }

    public boolean isError() {
        return this.error;
    }

    public int getCode() {
        return this.code;
    }
}
