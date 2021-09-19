package net.cryptic_game.backend.admin.repository.website;

import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.cryptic_game.backend.admin.model.website.BlogPostSmallModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostSmallRepository extends JpaRepository<BlogPostSmallModel, IdModel> {

    @Query("select object(bp) from BlogPostSmallModel bp where bp.id.language = ?1 order by bp.created desc")
    List<BlogPostSmallModel> findAll(String language);
}
