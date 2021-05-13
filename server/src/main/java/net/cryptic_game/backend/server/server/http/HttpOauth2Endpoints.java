package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.Constants;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserOAuth2ProviderId;
import net.cryptic_game.backend.data.sql.repositories.user.UserOAuthProviderIdRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.server.authentication.AuthenticationErrorException;
import net.cryptic_game.backend.server.authentication.OAuth2Config;
import net.cryptic_game.backend.server.authentication.OAuth2Provider;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "oauth2", type = ApiType.REST)
public final class HttpOauth2Endpoints {

    private final Key key;
    private final OAuth2Config oAuth2Config;
    private final OAuth2Provider oAuth2Provider;
    private final UserRepository userRepository;
    private final UserOAuthProviderIdRepository userOAuthProviderIdRepository;

    @ApiEndpoint(id = "info")
    public ApiResponse providerInfo(@ApiParameter(id = "provider") final String provider) {
        switch (provider) {
            case "discord":
                return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("provider",
                        JsonBuilder
                                .create("client_id", this.oAuth2Config.getDiscordClientId())
                                .add("redirect_uri", this.oAuth2Config.getDiscordRedirectUri())
                                .add("scopes", this.oAuth2Config.getDiscordScopes())
                ));
            case "github":
                return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("provider",
                        JsonBuilder
                                .create("client_id", this.oAuth2Config.getGithubClientId())
                                .add("redirect_uri", this.oAuth2Config.getGithubRedirectUri())
                                .add("scopes", this.oAuth2Config.getGithubScopes())
                ));
            default:
                return new ApiResponse(HttpResponseStatus.NOT_FOUND, "PROVIDER");
        }
    }

    @ApiEndpoint(id = "callback")
    public Mono<ApiResponse> callback(@ApiParameter(id = "provider") final String provider,
                                      @ApiParameter(id = "code") final String code) {
        final Mono<String> userIdMono;
        switch (provider) {
            case "discord":
                userIdMono = oAuth2Provider.discordUserId(code);
                break;
            case "github":
                userIdMono = oAuth2Provider.githubUserId(code);
                break;
            default:
                return Mono.just(new ApiResponse(HttpResponseStatus.NOT_FOUND, "PROVIDER"));
        }

        return userIdMono.flatMap(userId -> {
            final Optional<UserOAuth2ProviderId> providerUserId = this.userOAuthProviderIdRepository
                    .findById(new UserOAuth2ProviderId.ProviderUserId(userId, provider));

            final OffsetDateTime now = OffsetDateTime.now();
            User user;

            if (providerUserId.isPresent()) {
                user = providerUserId.get().getUser();
            } else {
                user = new User();
                user.setCreated(now);
                user.setLast(now);
                user.setNewUser(true);
                final UserOAuth2ProviderId oauth2ProviderUserId = new UserOAuth2ProviderId();
                oauth2ProviderUserId.setUser(this.userRepository.save(user));
                oauth2ProviderUserId.setProviderUserId(new UserOAuth2ProviderId.ProviderUserId(userId, provider));
                this.userOAuthProviderIdRepository.save(oauth2ProviderUserId);
            }

            return Mono.just(new ApiResponse(HttpResponseStatus.OK, JsonBuilder
                    .create("access_token", SecurityUtils.jwt(
                            this.key,
                            JsonBuilder.create("user_id", user.getId())
                                    .add("exp", now.plusSeconds(Constants.ACCESS_TOKEN_EXPIRE))
                                    .build()
                    ))
                    .add("new_user", user.isNewUser(), () -> true)
            ));
        }).onErrorReturn(AuthenticationErrorException.class, new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "BAD_OAUTH2_RESPONSE"));
    }
}
