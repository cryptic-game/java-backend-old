package net.cryptic_game.backend.admin.endpoints.server_managemnet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.AdminPanelConfig;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.data.Permission;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.data.sql.repositories.server_management.DisabledEndpointRepository;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "endpoints", description = "manage endpoints", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public final class EndpointManagement {

    private final BaseConfig baseConfig;
    private final AdminPanelConfig config;
    private final DisabledEndpointRepository disabledEndpointRepository;

    private HttpClient httpClient = null;

    @ApiEndpoint(id = "all", authentication = Permission.INTERNAL)
    public Mono<ApiResponse> getAllEndpoints() {
        return this.responseFromServer("/admin_panel/endpoints", new JsonObject());
    }

    @ApiEndpoint(id = "enable", authentication = Permission.SERVER_MANAGEMENT)
    public Mono<ApiResponse> enableEndpoint(@ApiParameter(id = "id") final String id) {
        return this.getAllEndpoints().flatMap(apiResponse -> {
            if (!this.disabledEndpointRepository.existsById(id)) {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_NOT_DISABLED"));
            }
            if (((JsonObject) apiResponse.getJson()).get(id) != null) {
                this.disabledEndpointRepository.deleteById(id);
                return this.responseFromServer("/admin_panel/enable", JsonBuilder.create("id", id).build());
            } else {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_DOES_NOT_EXIST"));
            }
        });
    }

    @ApiEndpoint(id = "disable", authentication = Permission.SERVER_MANAGEMENT)
    public Mono<ApiResponse> disableEndpoint(@ApiParameter(id = "id") final String id) {
        return this.getAllEndpoints().flatMap(apiResponse -> {
            if (this.disabledEndpointRepository.existsById(id)) {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_ALREADY_DISABLED"));
            }
            if (((JsonObject) apiResponse.getJson()).get(id) != null) {
                this.disabledEndpointRepository.create(id);
                return this.responseFromServer("/admin_panel/disable", JsonBuilder.create("id", id).build());
            } else {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_DOES_NOT_EXIST"));
            }
        });
    }

    private Mono<ApiResponse> responseFromServer(final String endpoint, final JsonObject data) {
        if (this.httpClient == null)
            this.httpClient = HttpClient.create()
                    .baseUrl(this.config.getServerAddress())
                    .headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, this.baseConfig.getApiToken()));
        return Mono.from(this.httpClient.post()
                .uri(endpoint)
                .send(Mono.just(Unpooled.copiedBuffer(data.toString(), StandardCharsets.UTF_8)))
                .responseSingle((client, receiver) -> {
                    return receiver.asString().
                            map(JsonParser::parseString)
                            .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                            .flatMap(content ->
                                    Mono.just(new ApiResponse(client.status(), content)));
                }));
    }
}
