package net.cryptic_game.backend.admin.dto.website;

import java.time.OffsetDateTime;

import lombok.Data;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;

@Data
public class BlogPostSmall {

    private final Id id;
    private final String title;
    private final String image;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final boolean published;
    private final String description;

    public BlogPostSmall(
            /* @JsonProperty("id") */ final Id id,
            /* @JsonProperty("title") */ final String title,
            /* @JsonProperty("image") */ final String image,
            /* @JsonProperty("created") */ final OffsetDateTime created,
            /* @JsonProperty("updated") */ final OffsetDateTime updated,
            /* @JsonProperty("published") */ final boolean published,
            /* @JsonProperty("description") */ final String description
    ) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.created = created;
        this.updated = updated;
        this.published = published;
        this.description = description;
    }
}
