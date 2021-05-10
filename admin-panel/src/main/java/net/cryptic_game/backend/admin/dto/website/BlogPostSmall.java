package net.cryptic_game.backend.admin.dto.website;

import lombok.Data;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;

import java.time.OffsetDateTime;

@Data
public class BlogPostSmall {

    private final Id id;
    private final String title;
    private final String image;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final String description;

    public BlogPostSmall(
            /* @JsonProperty("id") */ final Id id,
            /* @JsonProperty("title") */ final String title,
            /* @JsonProperty("image") */ final String image,
            /* @JsonProperty("created") */ final OffsetDateTime created,
            /* @JsonProperty("updated") */ final OffsetDateTime updated,
            /* @JsonProperty("description") */ final String description
    ) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.created = created;
        this.updated = updated;
        this.description = description;
    }
}
