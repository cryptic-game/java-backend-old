package net.cryptic_game.backend.admin.repository.website;

import java.util.UUID;

import net.cryptic_game.backend.admin.model.website.TeamDepartmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamDepartmentRepository extends JpaRepository<TeamDepartmentModel, UUID> {
}
