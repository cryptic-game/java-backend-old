package net.cryptic_game.backend.admin.model.website;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.getnova.framework.jpa.model.TableModel;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Setter
@Getter
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_blog_post")
public class BlogPostSmallModel extends TableModel {

    @EmbeddedId
    private IdModel id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "image", nullable = false)
    private String image;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    @Column(name = "updated")
    private OffsetDateTime updated;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "description", nullable = false, length = 1025)
    private String description;
}
