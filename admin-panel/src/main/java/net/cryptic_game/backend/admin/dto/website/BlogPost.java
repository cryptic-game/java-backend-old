package net.cryptic_game.backend.admin.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlogPost {

    private final Id id;
    private final String title;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final String description;
    private final String content;

    public BlogPost(
            @JsonProperty("id") final Id id,
            @JsonProperty("title") final String title,
            @JsonProperty("created") final OffsetDateTime created,
            @JsonProperty("updated") final OffsetDateTime updated,
            @JsonProperty("description") final String description,
            @JsonProperty("content") final String content
    ) {
        this.id = id;
        this.title = title;
        this.created = created;
        this.updated = updated;
        this.description = description;
        this.content = content;
    }

    @Data
    public static final class Id {

        private final String language; // TODO java.util.Locale;
        private final String postId;

        public Id(
                @JsonProperty("language") final String language,
                @JsonProperty("postId") final String postId
        ) {
            this.language = language;
            this.postId = postId;
        }
    }
}
