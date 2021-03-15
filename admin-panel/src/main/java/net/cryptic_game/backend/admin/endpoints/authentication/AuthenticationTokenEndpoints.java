package net.cryptic_game.backend.admin.endpoints.authentication;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.sql.entities.user.AdminUser;
import net.cryptic_game.backend.data.sql.repositories.user.AdminUserRepository;

import java.security.Key;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/token", description = "renew tokens", type = ApiType.REST)
public final class AuthenticationTokenEndpoints {

    private final Key key;
    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "refresh")
    public ApiResponse refresh(@ApiParameter(id = "refresh_token") final String refreshToken) {
        try {
            final JsonObject jsonObject = SecurityUtils.parseJwt(this.key, refreshToken);
            final AdminUser user = this.adminUserRepository.findById(JsonUtils.fromJson(jsonObject.get("sub"), Long.class)).orElse(null);
            assert user != null;
            return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("access_token", SecurityUtils.jwt(
                    this.key,
                    JsonBuilder.create("user_id", user.getId())
                            .add("groups", user.getGroups())
                            .add("exp", OffsetDateTime.now().plusMinutes(15))
                            .build()))
            );
        } catch (Exception e) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED);
        }
    }
}
