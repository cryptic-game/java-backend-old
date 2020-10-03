package net.cryptic_game.backend.admin.endpoints.authentication;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.Groups;
import net.cryptic_game.backend.admin.data.sql.entites.user.AdminUser;
import net.cryptic_game.backend.admin.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/user")
public class AdminUserEndpoints {

    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "add", groups = Groups.ADMIN_ID)
    public ApiResponse add(@ApiParameter(id = "id") final long id, @ApiParameter(id = "groups", required = false) final Set<String> groups) {
        final AdminUser user = new AdminUser();
        user.setId(id);
        if (groups == null) user.setGroups(Set.of(Groups.USER));
        else user.setGroups(groups.parallelStream().map(Groups::byId).filter(Objects::nonNull).collect(Collectors.toSet()));
        adminUserRepository.save(user);
        return new ApiResponse(ApiResponseStatus.OK);
    }
}
