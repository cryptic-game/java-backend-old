package net.cryptic_game.backend.server.server.http;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiType;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "daemon", description = "Endpoints for the daemon", apiType = ApiType.REST)
public final class HttpDaemonEndpoints {

//    private final Set<ApiClient> clients;
//    private final String apiToken;
//
//    //TODO Redis implementation
//    @ApiEndpoint(id = "notify", description = "Send a notification to all sessions of a user.")
//    public ApiResponse notify(@ApiParameter(id = "user_id") final UUID userId,
//                              @ApiParameter(id = "api_token", optional = true) final String apiToken,
//                              @ApiParameter(id ="topic") final String topic,
//                              @ApiParameter(id ="data") final JsonElement data) {
//        if (!(this.apiToken.isBlank() || this.apiToken.equals(apiToken))) return new ApiResponse(ApiResponseStatus.UNAUTHORIZED);
//
//        final Set<ApiClient> userClients = this.clients.stream().filter(client -> {
//            final Session session = client.get(Session.class);
//            return session != null && session.getUserId().equals(userId);
//        }).collect(Collectors.toUnmodifiableSet());
//
//        if (userClients.size() == 0) return new ApiResponse(ApiResponseStatus.NOT_FOUND, "USER_CLIENT");
//
//        userClients.forEach(client -> client.getChannel().writeAndFlush(new TextWebSocketFrame(new ApiNotification(topic, data).serialize().toString())));
//        return new ApiResponse(ApiResponseStatus.OK);
//    }
}
