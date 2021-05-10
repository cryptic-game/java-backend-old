package net.cryptic_game.backend.admin.repository.website;

import net.cryptic_game.backend.admin.model.website.TeamMemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberModel, UUID> {
}
