package net.cryptic_game.backend.admin.endpoints.authentication;

import io.netty.handler.codec.http.HttpResponseStatus;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.Group;
import net.cryptic_game.backend.data.Permission;

import java.util.Arrays;
import java.util.stream.Collectors;

@ApiEndpointCollection(id = "authentication/group",
        description = "Manage access groups for the admin panel",
        type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public class AdminGroupsEndpoints {

    @ApiEndpoint(id = "list", authentication = Permission.INTERNAL)
    private ApiResponse list() {
        return new ApiResponse(
                HttpResponseStatus.OK,
                Arrays.stream(Group.values())
                        .map(group -> JsonBuilder.create("id", group.getId())
                                .add("display_name", group.getDisplayName()))
                        .collect(Collectors.toSet())
        );
    }
}
