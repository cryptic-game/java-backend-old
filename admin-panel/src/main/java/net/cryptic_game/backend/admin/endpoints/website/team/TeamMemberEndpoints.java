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
@ApiEndpointCollection(id = "website/team/member", description = "manage team members", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
@RequiredArgsConstructor
public final class TeamMemberEndpoints {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamDepartmentRepository teamDepartmentRepository;

    @ApiEndpoint(id = "list")
    public ApiResponse list(@ApiParameter(id = "department_id", required = false) final UUID departmentId) {
        if (departmentId == null) return new ApiResponse(HttpResponseStatus.OK, this.teamMemberRepository.findAll());
        return new ApiResponse(HttpResponseStatus.OK, this.teamMemberRepository.findAllByDepartmentId(departmentId));
    }

    @ApiEndpoint(id = "add", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse add(@ApiParameter(id = "name") final String name,
                           @ApiParameter(id = "github_id") final long githubId,
                           @ApiParameter(id = "department_id") final UUID departmentId,
                           @ApiParameter(id = "joined") final OffsetDateTime joined) {
        if (this.teamMemberRepository.findByName(name).isPresent() || this.teamMemberRepository.findByGithubId(githubId).isPresent()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MEMBER_ALREADY_EXISTS");
        }

        final TeamDepartment department = this.teamDepartmentRepository.findById(departmentId).orElse(null);
        if (department == null) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEPARTMENT_NOT_FOUND");

        return new ApiResponse(HttpResponseStatus.OK, this.teamMemberRepository.createTeamMember(name, githubId, department, joined));
    }

    @ApiEndpoint(id = "update", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse update(@ApiParameter(id = "id") final UUID id,
                              @ApiParameter(id = "name", required = false) final String name,
                              @ApiParameter(id = "department_id", required = false) final UUID departmentId,
                              @ApiParameter(id = "joined", required = false) final OffsetDateTime joined) {

        final TeamMember member = this.teamMemberRepository.findById(id).orElse(null);
        if (member == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MEMBER_NOT_FOUND");
        }

        if (member.getJoined() != null) member.setName(name);

        if (member.getDepartment() != null) {
            final TeamDepartment department = this.teamDepartmentRepository.findById(departmentId).orElse(null);
            if (department == null) {
                return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEPARTMENT_NOT_FOUND");
            }

            member.setDepartment(department);
        }

        if (member.getJoined() != null) member.setJoined(joined);

        return new ApiResponse(HttpResponseStatus.OK, this.teamMemberRepository.save(member));
    }

    @ApiEndpoint(id = "delete", authentication = Permission.TEAM_MANAGEMENT)
    public ApiResponse delete(@ApiParameter(id = "id") final UUID id) {
        final TeamMember member = this.teamMemberRepository.findById(id).orElse(null);
        if (member == null) return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MEMBER_NOT_FOUND");
        this.teamMemberRepository.delete(member);
        return new ApiResponse(HttpResponseStatus.OK);
    }
}
