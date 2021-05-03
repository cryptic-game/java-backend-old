package net.cryptic_game.backend.admin.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

import lombok.Data;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;

@Data
public class BlogPostSmall {

    private final Id id;
    private final String title;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final String description;

    public BlogPostSmall(
            @JsonProperty("id") final Id id,
            @JsonProperty("title") final String title,
            @JsonProperty("created") final OffsetDateTime created,
            @JsonProperty("updated") final OffsetDateTime updated,
            @JsonProperty("description") final String description
    ) {
        this.id = id;
        this.title = title;
        this.created = created;
        this.updated = updated;
        this.description = description;
    }
}
