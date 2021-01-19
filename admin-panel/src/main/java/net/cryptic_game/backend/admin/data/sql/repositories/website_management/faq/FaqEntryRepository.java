package net.cryptic_game.backend.admin.data.sql.repositories.website_management.faq;

import net.cryptic_game.backend.admin.data.sql.entities.website_management.faq.FaqEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FaqEntryRepository extends JpaRepository<FaqEntry, UUID> {

    Optional<FaqEntry> getByQuestion(String question);
}
