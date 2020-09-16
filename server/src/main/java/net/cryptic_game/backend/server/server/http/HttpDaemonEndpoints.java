package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.api.notification.ApiNotification;
import net.cryptic_game.backend.data.redis.entities.Session;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public final class HttpDaemonEndpoints extends ApiEndpointCollection {

    private final Set<ApiClient> clients;
    private final String apiToken;

    public HttpDaemonEndpoints(final Set<ApiClient> clients, final String apiToken) {
        super("daemon", "Endpoints for the daemon");
        this.clients = clients;
        this.apiToken = apiToken;
    }

    //TODO Redis implementation
    @ApiEndpoint(value = "notify", description = "Send a notification to all sessions of a user.")
    public ApiResponse notify(@ApiParameter(value = "user_id") final UUID userId,
                              @ApiParameter(value = "api_token", optional = true) final String apiToken,
                              @ApiParameter("topic") final String topic,
                              @ApiParameter("data") final JsonElement data) {
        if (!(this.apiToken.isBlank() || this.apiToken.equals(apiToken))) return new ApiResponse(ApiResponseType.UNAUTHORIZED);

        final Set<ApiClient> userClients = this.clients.stream().filter(client -> {
            final Session session = client.get(Session.class);
            return session != null && session.getUserId().equals(userId);
        }).collect(Collectors.toUnmodifiableSet());

        if (userClients.size() == 0) return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_CLIENT");

        userClients.forEach(client -> client.getChannel().writeAndFlush(new TextWebSocketFrame(new ApiNotification(topic, data).serialize().toString())));
        return new ApiResponse(ApiResponseType.OK);
    }
}
