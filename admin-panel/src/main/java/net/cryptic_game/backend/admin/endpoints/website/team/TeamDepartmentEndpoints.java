package net.cryptic_game.backend.admin.endpoints.website.team;


import io.netty.handler.codec.http.HttpResponseStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.admin.authentication.Permission;
import net.cryptic_game.backend.admin.data.sql.entities.website.team.TeamDepartment;
import net.cryptic_game.backend.admin.data.sql.entities.website.team.TeamMember;
import net.cryptic_game.backend.admin.data.sql.repositories.website.team.TeamDepartmentRepository;
import net.cryptic_game.backend.admin.data.sql.repositories.website.team.TeamMemberRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import org.springframework.stereotype.Component;

@Component
@ApiEndpointCollection(id = "website/team/department", description = "manages team departments", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
@RequiredArgsConstructor
public final class TeamDepartmentEndpoints {

    private final TeamDepartmentRepository teamDepartmentRepository;
    private final TeamMemberRepository teamMemberRepository;

    @ApiEndpoint(id = "list")
    public ApiResponse list() {
        return new ApiResponse(HttpResponseStatus.OK, this.teamDepartmentRepository.findAll());
    }

    @ApiEndpoint(id = "add", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse add(@ApiParameter(id = "name") final String name,
                           @ApiParameter(id = "description") final String description) {
        if (this.teamDepartmentRepository.findByName(name).isPresent()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "DEPARTMENT_ALREADY_EXISTS");
        }
        return new ApiResponse(HttpResponseStatus.OK, this.teamDepartmentRepository.createTeamDepartment(name, description));
    }

    @ApiEndpoint(id = "update", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse update(@ApiParameter(id = "id") final UUID id,
                              @ApiParameter(id = "name") final String name) {

        final TeamDepartment department = this.teamDepartmentRepository.findById(id).orElse(null);
        if (department == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MEMBER_NOT_FOUND");
        }

        department.setName(name);

        return new ApiResponse(HttpResponseStatus.OK, this.teamDepartmentRepository.save(department));
    }

    @ApiEndpoint(id = "delete", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse delete(@ApiParameter(id = "department_id") final UUID departmentId) {
        final TeamDepartment department = this.teamDepartmentRepository.findById(departmentId).orElse(null);
        if (department == null) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEPARTMENT_NOT_FOUND");
        this.teamMemberRepository.findAllByDepartmentId(departmentId).forEach(this.teamMemberRepository::delete);
        this.teamDepartmentRepository.delete(department);
        return new ApiResponse(HttpResponseStatus.OK);
    }
}
