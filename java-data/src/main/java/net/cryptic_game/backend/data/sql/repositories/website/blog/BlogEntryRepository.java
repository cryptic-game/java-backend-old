package net.cryptic_game.backend.data.sql.repositories.website.blog;

import net.cryptic_game.backend.data.sql.entities.website.blog.BlogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BlogEntryRepository extends JpaRepository<BlogEntry, UUID> {

    Page<BlogEntry> findAllByOrderByCreationDesc(Pageable pageable);

    Page<BlogEntry> findAllByLanguageOrderByCreationDesc(String language, Pageable pageable);
}
