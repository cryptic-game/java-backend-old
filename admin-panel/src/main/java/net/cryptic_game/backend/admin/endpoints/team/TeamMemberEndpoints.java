package net.cryptic_game.backend.admin.endpoints.team;

import net.cryptic_game.backend.admin.data.sql.entities.team.TeamDepartment;
import net.cryptic_game.backend.admin.data.sql.entities.team.TeamMember;
import net.cryptic_game.backend.admin.data.sql.repositories.team.TeamDepartmentRepository;
import net.cryptic_game.backend.admin.data.sql.repositories.team.TeamMemberRepository;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public final class TeamMemberEndpoints extends ApiEndpointCollection {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamDepartmentRepository teamDepartmentRepository;

    public TeamMemberEndpoints(final TeamMemberRepository teamMemberRepository, final TeamDepartmentRepository teamDepartmentRepository) {
        super("team/member", "manage team members");
        this.teamMemberRepository = teamMemberRepository;
        this.teamDepartmentRepository = teamDepartmentRepository;
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "department_id", optional = true) final UUID departmentId) {
        if (departmentId == null) return new ApiResponse(ApiResponseType.OK, this.teamMemberRepository.findAll());
        return new ApiResponse(ApiResponseType.OK, this.teamMemberRepository.findAllByDepartmentId(departmentId));
    }

    @ApiEndpoint("add")
    public ApiResponse add(@ApiParameter("name") final String name,
                           @ApiParameter("github_id") final long githubId,
                           @ApiParameter("department_id") final UUID departmentId,
                           @ApiParameter("joined") final OffsetDateTime joined) {
        if (this.teamMemberRepository.findByName(name).isPresent() || this.teamMemberRepository.findByGithubId(githubId).isPresent()) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MEMBER_ALREADY_EXISTS");
        }

        final TeamDepartment department = this.teamDepartmentRepository.findById(departmentId).orElse(null);
        if (department == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "DEPARTMENT_NOT_FOUND");

        return new ApiResponse(ApiResponseType.OK, this.teamMemberRepository.createTeamMember(name, githubId, department, joined));
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter("member_id") final UUID memberId) {
        final TeamMember member = this.teamMemberRepository.findById(memberId).orElse(null);
        if (member == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER_NOT_FOUND");
        this.teamMemberRepository.delete(member);
        return new ApiResponse(ApiResponseType.OK);
    }
}
