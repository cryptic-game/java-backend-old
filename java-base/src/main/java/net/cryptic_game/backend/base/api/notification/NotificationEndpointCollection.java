package net.cryptic_game.backend.base.api.notification;

import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.utils.JsonBuilder;

public class NotificationEndpointCollection extends ApiEndpointCollection {

    public NotificationEndpointCollection() {
        super("notification");
    }

    @ApiEndpoint("subscribe")
    public ApiResponse subscribe(
            final ApiClient client,
            @ApiParameter("topic") final String topic) {

        client.subscribe(topic);
        return new ApiResponse(ApiResponseType.OK, JsonBuilder.simple("success", true));
    }

    @ApiEndpoint("unsubscribe")
    public ApiResponse unsubscribe(
            final ApiClient client,
            @ApiParameter("topic") final String topic) {

        client.unsubscribe(topic);
        return new ApiResponse(ApiResponseType.OK, JsonBuilder.simple("success", true));
    }
}
