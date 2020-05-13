package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.api.notification.ApiNotification;
import net.cryptic_game.backend.data.user.Session;
import net.cryptic_game.backend.server.App;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DaemonUserEndpoints extends ApiEndpointCollection {

    public DaemonUserEndpoints() {
        super("user");
    }

    @ApiEndpoint("send")
    public ApiResponse send(@ApiParameter("user_id") final UUID userId, @ApiParameter("topic") final String topic, @ApiParameter("data") final JsonObject data) {

        Set<Channel> channels = ((App) App.getInstance()).getWebSocketEndpointHandler().getApiList().getClientList()
                .filter((apiClient -> apiClient.get(Session.class) != null && apiClient.get(Session.class).getUser() != null
                        && apiClient.get(Session.class).getUser().getId().equals(userId)))
                .stream().map((ApiClient::getChannel)).collect(Collectors.toSet());
        new ApiNotification(topic.toUpperCase(), data).send(channels);
        return new ApiResponse(ApiResponseType.OK);
    }
}
