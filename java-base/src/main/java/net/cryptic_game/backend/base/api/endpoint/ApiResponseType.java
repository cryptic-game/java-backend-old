package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
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
    GATEWAY_TIMEOUT(504, "GATEWAY_TIMEOUT", true),

    // Self made
    ALREADY_EXISTS(905, "ALREADY_EXIST", true);

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
        final JsonBuilder json = JsonBuilder.create("code", this.getCode())
                .add("name", this.getName())
                .add("error", this.isError());
        if (showNotification) json.add("notification", false);
        return json.build();
    }

    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public boolean isError() {
        return this.error;
    }

    @Override
    public String toString() {
        return "ApiResponseType{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", error=" + error +
                '}';
    }
}
