package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonUtils;

@Getter
@EqualsAndHashCode
public final class ApiResponse {

    private final HttpResponseStatus status;
    private final String error;
    private final JsonElement json;

    @Setter
    private String tag;

    public ApiResponse(final HttpResponseStatus status) {
        this.status = status;
        this.error = null;
        this.json = null;
    }

    public ApiResponse(final HttpResponseStatus status, final String error) {
        this.status = status;
        this.error = error;
        this.json = null;
    }

    public ApiResponse(final HttpResponseStatus status, final Object json) {
        this.status = status;
        this.error = null;
        this.json = JsonUtils.toJson(json);
    }
}
