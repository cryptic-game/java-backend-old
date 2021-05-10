package net.cryptic_game.backend.admin.repository.website;

import java.util.List;

import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.cryptic_game.backend.admin.model.website.BlogPostSmallModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import org.w3c.dom.stylesheets.LinkStyle;

@Repository
public interface BlogPostSmallRepository extends JpaRepository<BlogPostSmallModel, IdModel> {

    @Query("select object(bp) from BlogPostSmallModel bp where bp.id.language = ?0 order by bp.created")
    List<BlogPostSmallModel> findAll(String language);
}
