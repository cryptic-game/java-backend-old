package net.cryptic_game.backend.admin.endpoints.authentication;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.Groups;
import net.cryptic_game.backend.admin.data.sql.entities.user.AdminUser;
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
public final class AdminUserEndpoints {

    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "add", groups = Groups.ADMIN_ID)
    public ApiResponse add(@ApiParameter(id = "id") final long id,
                           @ApiParameter(id = "name", required = false) final String name,
                           @ApiParameter(id = "groups", required = false) final Set<String> groups) {
        if (this.adminUserRepository.findById(id).isPresent()) return new ApiResponse(ApiResponseStatus.CONFLICT, "USER_ALREADY_EXISTS");
        final AdminUser user = new AdminUser();
        user.setId(id);
        user.setName(name);
        if (groups == null || groups.size() == 0) user.setGroups(Set.of(Groups.USER));
        else user.setGroups(groups.parallelStream().map(Groups::byId).filter(Objects::nonNull).collect(Collectors.toSet()));
        return new ApiResponse(ApiResponseStatus.OK, this.adminUserRepository.save(user));
    }

    @ApiEndpoint(id = "delete", groups = Groups.ADMIN_ID)
    public ApiResponse delete(@ApiParameter(id = "id") final long id) {
        if (this.adminUserRepository.findById(id).isEmpty()) return new ApiResponse(ApiResponseStatus.NOT_FOUND, "USER");
        this.adminUserRepository.deleteById(id);
        return new ApiResponse(ApiResponseStatus.OK);
    }

    @ApiEndpoint(id = "list", groups = Groups.ADMIN_ID)
    public ApiResponse list() {
        return new ApiResponse(ApiResponseStatus.OK, this.adminUserRepository.findAll());
    }

    @ApiEndpoint(id = "update", groups = Groups.ADMIN_ID)
    public ApiResponse update(@ApiParameter(id = "id") final long id,
                              @ApiParameter(id = "groups") final Set<String> groups) {
        final AdminUser user = this.adminUserRepository.findById(id).orElse(null);
        if (user == null) return new ApiResponse(ApiResponseStatus.NOT_FOUND, "USER");
        if (groups.size() == 0) user.setGroups(Set.of(Groups.USER));
        else user.setGroups(groups.parallelStream().map(Groups::byId).filter(Objects::nonNull).collect(Collectors.toSet()));
        return new ApiResponse(ApiResponseStatus.OK, this.adminUserRepository.save(user));
    }
}
