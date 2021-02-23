package net.cryptic_game.backend.data.sql.repositories.website.team;

import net.cryptic_game.backend.data.sql.entities.website.team.TeamDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamDepartmentRepository extends JpaRepository<TeamDepartment, UUID> {

    Optional<TeamDepartment> findByName(String name);

    default TeamDepartment createTeamDepartment(final String name, final String description) {
        return this.save(new TeamDepartment(name, description));
    }
}
