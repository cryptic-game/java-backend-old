package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiRequest {

    private final String endpoint;
    private final JsonObject data;
    private String tag;
    private ApiEndpointData endpointData;

    public ApiRequest(final String endpoint, final JsonObject data, final String tag) {
        this.endpoint = endpoint;
        this.data = data;
        this.tag = tag;
    }
}
