package net.cryptic_game.backend.base.api.handler.rest;

import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cryptic_game.backend.base.api.data.ApiRequest;

@Getter
@ToString
@EqualsAndHashCode
public class RestApiRequest extends ApiRequest {

    private final RestApiContext context;

    public RestApiRequest(final String endpoint, final JsonObject data, final RestApiContext context) {
        super(endpoint, data);
        this.context = context;
    }
}
