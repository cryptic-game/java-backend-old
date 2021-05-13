package net.cryptic_game.backend.admin.controller.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.dto.website.TeamDepartment;
import net.cryptic_game.backend.admin.dto.website.TeamMember;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.service.website.TeamService;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TeamControllerImpl implements TeamController {

    private final TeamService teamService;

    @Override
    public Set<TeamMember> findMembers() {
        return this.teamService.findMembers();
    }

    @Override
    public TeamMember findMember(final UUID memberId) {
        return this.teamService.findMember(memberId)
                .orElseThrow(() -> new NotFoundException(memberId, "MEMBER_NOT_FOUND"));
    }

    @Override
    public TeamMember postMember(final TeamMember member) {
        return this.teamService.saveMember(member);
    }

    @Override
    public TeamMember putMember(final UUID memberId, final TeamMember member) {
        return this.teamService.saveMember(memberId, member);
    }

    @Override
    public void deleteMember(final UUID memberId) {
        this.teamService.deleteMember(memberId);
    }

    @Override
    public Set<TeamDepartment> findDepartments() {
        return this.teamService.findDepartments();
    }

    @Override
    public TeamDepartment findDepartment(final UUID departmentId) {
        return this.teamService.findDepartment(departmentId)
                .orElseThrow(() -> new NotFoundException(departmentId, "DEPARTMENT_NOT_FOUND"));
    }

    @Override
    public TeamDepartment postDepartment(final TeamDepartment department) {
        return this.teamService.saveDepartment(department);
    }

    @Override
    public TeamDepartment putDepartment(final UUID departmentId, final TeamDepartment department) {
        return this.teamService.saveDepartment(departmentId, department);
    }

    @Override
    public void deleteDepartment(final UUID departmentId) {
        this.teamService.deleteDepartment(departmentId);
    }
}
