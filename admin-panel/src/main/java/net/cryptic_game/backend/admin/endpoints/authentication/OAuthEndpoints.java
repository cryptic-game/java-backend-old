package net.cryptic_game.backend.admin.endpoints.authentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AuthenticationErrorException;
import net.cryptic_game.backend.admin.authentication.OAuthConfig;
import net.cryptic_game.backend.admin.data.sql.entities.user.AdminUser;
import net.cryptic_game.backend.admin.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/oauth", description = "login with github", type = ApiType.REST)
public final class OAuthEndpoints {

    private final OAuthConfig config;
    private final Key key;
    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "callback")
    public Mono<ApiResponse> callback(@ApiParameter(id = "code") final String code) {
        return HttpClient.create()
                .headers(h -> h.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .send(Mono.just(Unpooled.wrappedBuffer(JsonBuilder.create("client_id", this.config.getClientId())
                        .add("client_secret", this.config.getClientSecret())
                        .add("code", code).build().toString().getBytes())))
                .responseContent()
                .aggregate()
                .asString()
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("access_token")) return Mono.error(new AuthenticationErrorException());
                    return HttpClient.create()
                            .headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, "token " + JsonUtils.fromJson(response.get("access_token"), String.class)))
                            .get()
                            .uri("https://api.github.com/user")
                            .responseContent()
                            .aggregate()
                            .asString();
                })
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("id")) return Mono.error(new AuthenticationErrorException());
                    long id = JsonUtils.fromJson(response.get("id"), Long.class);
                    final AdminUser user = this.adminUserRepository.findById(id).orElse(null);
                    if (user == null) return Mono.just(new ApiResponse(HttpResponseStatus.FORBIDDEN));
                    final String name = Optional.ofNullable(JsonUtils.fromJson(response.get("name"), String.class))
                            .orElseGet(() -> JsonUtils.fromJson(response.get("login"), String.class));
                    this.adminUserRepository.save(user);

                    final OffsetDateTime now = OffsetDateTime.now();
                    return Mono.just(new ApiResponse(HttpResponseStatus.OK, JsonBuilder
                                    .create("access_token", SecurityUtils.jwt(
                                            this.key,
                                            JsonBuilder.create("user_id", id)
                                                    .add("groups", user.getGroups())
                                                    .add("exp", now.plusMinutes(15))
                                                    .build()))
                                    .add("refresh_token", SecurityUtils.jwt(
                                            this.key,
                                            JsonBuilder.create("sub", id)
                                                    .add("exp", now.plusWeeks(1))
                                                    .add("iat", now)
                                                    .add("name", name)
                                                    .add("refresh_token", true)
                                                    .build()
                                    ))
                            )
                    );
                })
                .onErrorReturn(AuthenticationErrorException.class, new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "BAD_GITHUB_RESPONSE"));
    }

    @ApiEndpoint(id = "client_id")
    public ApiResponse clientId() {
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("client_id", this.config.getClientId()));
    }
}
