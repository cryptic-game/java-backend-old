package net.cryptic_game.backend.data.sql.entities.website.blog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "blog_entry")
public class BlogEntry extends TableModelAutoId {

    @Column(name = "creation", updatable = true, nullable = false)
    private OffsetDateTime creation;

    @Column(name = "updated", updatable = true, nullable = true)
    private OffsetDateTime updated;

    @Column(name = "title", updatable = true, nullable = false)
    private String title;

    @Column(name = "language", updatable = false, nullable = false)
    private String language;

    @Column(name = "content", updatable = true, nullable = false, columnDefinition = "TEXT")
    private String content;

}
