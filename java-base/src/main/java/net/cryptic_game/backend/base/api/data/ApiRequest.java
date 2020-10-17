package net.cryptic_game.backend.base.api.data;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ApiRequest {

    private final String endpoint;
    private final JsonObject data;
    private String tag;
}
