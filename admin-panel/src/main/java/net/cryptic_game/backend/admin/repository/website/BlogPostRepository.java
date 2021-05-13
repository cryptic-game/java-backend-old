package net.cryptic_game.backend.admin.repository.website;

import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostModel, IdModel> {
}
