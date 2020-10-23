package net.cryptic_game.backend.admin.endpoints.authentication;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.admin.authentication.Group;
import net.cryptic_game.backend.admin.authentication.Permission;
import net.cryptic_game.backend.admin.data.sql.entities.user.AdminUser;
import net.cryptic_game.backend.admin.data.sql.repositories.user.AdminUserRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.rest.RestApiRequest;
import net.cryptic_game.backend.base.json.JsonUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/user", description = "Manage access for the admin panel", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public final class AdminUserEndpoints {

    private final AdminUserRepository adminUserRepository;

    @ApiEndpoint(id = "add", authentication = Permission.ACCESS_MANAGEMENT)
    public ApiResponse add(@ApiParameter(id = "id") final long id,
                           @ApiParameter(id = "groups", required = false) final Set<String> groups) {
        if (this.adminUserRepository.findById(id).isPresent()) return new ApiResponse(HttpResponseStatus.CONFLICT, "USER_ALREADY_EXISTS");
        final AdminUser user = new AdminUser();
        user.setId(id);
        if (groups == null || groups.size() == 0) user.setGroups(Set.of(Group.USER));
        else {
            final Set<Group> collect = groups.parallelStream().map(Group::byId).filter(Objects::nonNull).collect(Collectors.toSet());
            user.setGroups(collect.size() > 0 ? collect : Set.of(Group.USER));
        }
        return new ApiResponse(HttpResponseStatus.OK, this.adminUserRepository.save(user));
    }

    @ApiEndpoint(id = "delete", authentication = Permission.ACCESS_MANAGEMENT)
    public ApiResponse delete(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final RestApiRequest request,
                              @ApiParameter(id = "id") final long id) {
        final Optional<AdminUser> user = this.adminUserRepository.findById(id);
        if (user.isEmpty()) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        if (user.get().getId() == JsonUtils.fromJson(request.getContext().get(JsonObject.class).orElseThrow().get("user_id"), Long.class)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "CANNOT_DELETE_OWN_USER");
        }
        this.adminUserRepository.deleteById(id);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "list", authentication = Permission.ACCESS_MANAGEMENT)
    public ApiResponse list() {
        return new ApiResponse(HttpResponseStatus.OK, this.adminUserRepository.findAll());
    }

    @ApiEndpoint(id = "update", authentication = Permission.ACCESS_MANAGEMENT)
    public ApiResponse update(@ApiParameter(id = "id") final long id,
                              @ApiParameter(id = "groups") final Set<String> groups) {
        final AdminUser user = this.adminUserRepository.findById(id).orElse(null);
        if (user == null) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER");
        if (groups.size() == 0) user.setGroups(Set.of(Group.USER));
        else user.setGroups(groups.parallelStream().map(Group::byId).filter(Objects::nonNull).collect(Collectors.toSet()));
        return new ApiResponse(HttpResponseStatus.OK, this.adminUserRepository.save(user));
    }
}
