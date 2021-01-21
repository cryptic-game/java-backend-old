package net.cryptic_game.backend.admin.data.sql.repositories.website.blog;

import net.cryptic_game.backend.admin.data.sql.entities.website.blog.BlogEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlogEntryRepository extends JpaRepository<BlogEntry, UUID> {
}
