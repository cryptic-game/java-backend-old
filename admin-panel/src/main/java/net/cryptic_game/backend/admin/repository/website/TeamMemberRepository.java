package net.cryptic_game.backend.admin.repository.website;

import java.util.UUID;

import net.cryptic_game.backend.admin.model.website.TeamMemberModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMemberModel, UUID> {
}
