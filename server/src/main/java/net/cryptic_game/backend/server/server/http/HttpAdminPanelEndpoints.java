package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiService;
import net.cryptic_game.backend.base.json.JsonBuilder;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "admin_panel", description = "Endpoints for the admin panel", type = ApiType.REST, authenticator = HttpServerAuthenticator.class)
public final class HttpAdminPanelEndpoints {

    private final Bootstrap bootstrap;
    private WebsocketApiService websocketApiService = null;

    @ApiEndpoint(id = "endpoints")
    public ApiResponse getEndpoints() {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("endpoints", this.websocketApiService.getEndpoints()));
    }

    @ApiEndpoint(id = "enable")
    public ApiResponse enableEndpoint(@ApiParameter(id = "id") final String id) {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        final ApiEndpointData endpointData = this.websocketApiService.getEndpoints().get(id);
        endpointData.setDisabled(false);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("endpoint", endpointData));
    }

    @ApiEndpoint(id = "disable")
    public ApiResponse disableEndpoint(@ApiParameter(id = "id") final String id) {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        final ApiEndpointData endpointData = this.websocketApiService.getEndpoints().get(id);
        endpointData.setDisabled(true);
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("endpoint", endpointData));
    }

}
