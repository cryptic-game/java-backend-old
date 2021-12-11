package net.cryptic_game.backend.admin.model.website;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import de.m4rc3l.nova.jpa.model.TableModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_blog_post")
public class BlogPostModel extends TableModel {

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

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class IdModel implements Serializable {

        @Column(name = "language", updatable = false, nullable = false)
        private String language; // TODO java.util.Locale

        @Column(name = "post_id", updatable = false, nullable = false)
        private String postId;
    }
}
