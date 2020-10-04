package net.cryptic_game.backend.admin.endpoints.authentication;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.data.sql.entites.user.AdminUser;
import net.cryptic_game.backend.admin.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;

import java.security.Key;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/token")
public class AuthenticationTokenEndpoints {

    private final Key key;
    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "refresh")
    public ApiResponse refresh(@ApiParameter(id = "refresh_token") final String refreshToken) {
        try {
            final AdminUser user = adminUserRepository.findById(JsonUtils.fromJson(SecurityUtils.parseJwt(key, refreshToken).get("user_id"), Long.class)).orElse(null);
            assert user != null;
            return new ApiResponse(ApiResponseStatus.OK, JsonBuilder.create("access_token", SecurityUtils.jwt(
                    key,
                    JsonBuilder.create("user_id", user.getId())
                            .add("name", user.getName())
                            .add("groups", user.getGroups())
                            .add("exp", OffsetDateTime.now().plusMinutes(1))
                            .build()))
            );
        } catch (Exception e) {
            return new ApiResponse(ApiResponseStatus.UNAUTHORIZED);
        }
    }
}
