package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
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
import java.util.List;
import java.util.Optional;

@ApiEndpointCollection(id = "oauth2", type = ApiType.REST)
public final class HttpOauth2Endpoints {

    private final Key key;
    private final OAuth2Provider oAuth2Provider;
    private final UserRepository userRepository;
    private final UserOAuthProviderIdRepository userOAuthProviderIdRepository;
    private final ApiResponse apiResponse;

    public HttpOauth2Endpoints(final Key key,
                               final OAuth2Config oAuth2Config,
                               final OAuth2Provider oAuth2Provider,
                               final UserRepository userRepository,
                               final UserOAuthProviderIdRepository userOAuthProviderIdRepository) {
        this.key = key;
        this.oAuth2Provider = oAuth2Provider;
        this.userRepository = userRepository;
        this.userOAuthProviderIdRepository = userOAuthProviderIdRepository;
        this.apiResponse = new ApiResponse(HttpResponseStatus.OK, List.of(
                JsonBuilder
                        .create("id", "discord")
                        .add("display_name", "Discord")
                        .add("auth_uri", oAuth2Config.getDiscordAuthUri()),
                JsonBuilder
                        .create("id", "github")
                        .add("display_name", "GitHub")
                        .add("auth_uri", oAuth2Config.getGithubAuthUri())
        ));
    }

    @ApiEndpoint(id = "list")
    public ApiResponse list() {
        return this.apiResponse;
    }

    @ApiEndpoint(id = "callback")
    public Mono<ApiResponse> callback(@ApiParameter(id = "provider") final String provider,
                                      @ApiParameter(id = "code") final String code) {
        final Mono<String> userIdMono;
        switch (provider) {
            case "discord":
                userIdMono = this.oAuth2Provider.discordUserId(code);
                break;
            case "github":
                userIdMono = this.oAuth2Provider.githubUserId(code);
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
