package net.cryptic_game.backend.server.server.http.endpoints;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.api.notification.ApiNotification;
import net.cryptic_game.backend.data.user.Session;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class HttpDaemonEndpoints extends ApiEndpointCollection {

    private final Set<ApiClient> clients;

    public HttpDaemonEndpoints(final Set<ApiClient> clients) {
        super("daemon", "Endpoints for the daemon");
        this.clients = clients;
    }

    //TODO Redis implementation
    @ApiEndpoint("notify")
    public ApiResponse notify(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("topic") final String topic,
                              @ApiParameter("data") final JsonElement data) {
        Set<ApiClient> userClients = this.clients.stream().filter(client -> {
            final Session session = client.get(Session.class);
            return session != null && session.getUser().getId().equals(userId);
        }).collect(Collectors.toUnmodifiableSet());

        if (userClients.size() == 0) return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_CLIENT");

        userClients.forEach(client -> client.getChannel().writeAndFlush(new TextWebSocketFrame(new ApiNotification(topic, data).serialize().toString())));
        return new ApiResponse(ApiResponseType.OK);
    }
}
