package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiException;
import net.cryptic_game.backend.base.api.client.ApiClient;

public class ApiEndpointFinder {

    private final ApiEndpointList apiList;

    public ApiEndpointFinder(final ApiEndpointList apiList) {
        this.apiList = apiList;
    }

    public ApiResponse execute(final ApiClient client, final JsonElement tag, final String endpoint, final JsonObject data) throws ApiException {
        if (endpoint == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST);
        } else {
            final ApiEndpointExecutor api = this.apiList.get(endpoint);
            if (api == null) {
                return new ApiResponse(ApiResponseType.NOT_FOUND);
            } else {
                try {
                    return api.execute(client, tag == null ? null : tag.getAsString(), data);
                } catch (ApiEndpointParameterException e) {
                    return new ApiResponse(ApiResponseType.BAD_REQUEST, e.getMessage());
                }
            }
        }
    }
}
