package net.cryptic_game.backend.admin.endpoints.team;

import net.cryptic_game.backend.admin.data.sql.entities.team.TeamDepartment;
import net.cryptic_game.backend.admin.data.sql.repositories.team.TeamDepartmentRepository;
import net.cryptic_game.backend.admin.data.sql.repositories.team.TeamMemberRepository;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class TeamDepartmentEndpoints extends ApiEndpointCollection {

    private final TeamDepartmentRepository teamDepartmentRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamDepartmentEndpoints(final TeamDepartmentRepository teamDepartmentRepository, final TeamMemberRepository teamMemberRepository) {
        super("team/department", "manages team departments");
        this.teamDepartmentRepository = teamDepartmentRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @ApiEndpoint("list")
    public ApiResponse list() {
        return new ApiResponse(ApiResponseType.OK, this.teamDepartmentRepository.findAll());
    }

    @ApiEndpoint("add")
    public ApiResponse add(@ApiParameter("name") final String name,
                           @ApiParameter("description") final String description) {
        if (this.teamDepartmentRepository.findByName(name).isPresent()) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "DEPARTMENT_ALREADY_EXISTS");
        }
        return new ApiResponse(ApiResponseType.OK, this.teamDepartmentRepository.createTeamDepartment(name, description));
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter("department_id") final UUID departmentId) {
        final TeamDepartment department = this.teamDepartmentRepository.findById(departmentId).orElse(null);
        if (department == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "DEPARTMENT_NOT_FOUND");
        this.teamMemberRepository.findAllByDepartmentId(departmentId).forEach(this.teamMemberRepository::delete);
        this.teamDepartmentRepository.delete(department);
        return new ApiResponse(ApiResponseType.OK);
    }
}
