package net.cryptic_game.backend.admin.service.website;

import net.cryptic_game.backend.dto.website.TeamDepartment;
import net.cryptic_game.backend.dto.website.TeamMember;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface TeamService {

    Set<TeamMember> findMembers();

    Optional<TeamMember> findMember(UUID id);

    TeamMember saveMember(UUID id, TeamMember member);

    TeamMember saveMember(TeamMember member);

    void deleteMember(UUID id);

    Set<TeamDepartment> findDepartments();

    Optional<TeamDepartment> findDepartment(UUID id);

    TeamDepartment saveDepartment(UUID id, TeamDepartment department);

    TeamDepartment saveDepartment(TeamDepartment department);

    void deleteDepartment(UUID id);
}
