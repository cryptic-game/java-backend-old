package net.cryptic_game.backend.admin.endpoints.user;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.Permission;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSuspensionRevokedRepository;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "user/suspension/revoked", description = "manages user suspensions", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public class UserSuspensionRevokedEndpoints {

    private final UserRepository userRepository;
    private final UserSuspensionRevokedRepository userSuspensionRevokedRepository;

    @ApiEndpoint(id = "list", authentication = Permission.USER_MANAGEMENT)
    public ApiResponse list(@ApiParameter(id = "user_id", required = false) final UUID userId,
                            @ApiParameter(id = "page") final int page,
                            @ApiParameter(id = "page_size") final int pageSize) {
        if (userId == null)
            return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRevokedRepository.findAll(PageRequest.of(page, pageSize)));

        final Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        return new ApiResponse(HttpResponseStatus.OK, this.userSuspensionRevokedRepository.findAllByUserSuspensionUserId(userId, PageRequest.of(page, pageSize)));
    }
}
