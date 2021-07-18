package net.cryptic_game.backend.admin.repository.website;

import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPostModel, IdModel> {

    @Query("select bp.id.language from BlogPostModel bp where bp.id.postId = ?1")
    Set<String> findLanguagesById(String postId);
}
