package net.cryptic_game.backend.data.sql.repositories.website.team;

import net.cryptic_game.backend.data.sql.entities.website.team.TeamDepartment;
import net.cryptic_game.backend.data.sql.entities.website.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

    List<TeamMember> findAllByDepartmentId(UUID departmentId);

    Optional<TeamMember> findByName(String name);

    Optional<TeamMember> findByGithubId(long githubName);

    default TeamMember createTeamMember(final String name, final long githubId, final TeamDepartment department, final OffsetDateTime joined) {
        return this.save(new TeamMember(name, githubId, department, joined));
    }
}
