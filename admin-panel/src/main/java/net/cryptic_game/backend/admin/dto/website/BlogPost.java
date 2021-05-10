package net.cryptic_game.backend.admin.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.time.OffsetDateTime;

@Data
public class BlogPost {

    private static final Whitelist WHITELIST = Whitelist.relaxed()
            .addEnforcedAttribute("a", "target", "_blank")
            .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");

    private final Id id;
    private final String title;
    private final String image;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final String description;
    private final String content;

    public BlogPost(
            @JsonProperty("id") final Id id,
            @JsonProperty("title") final String title,
            @JsonProperty("image") final String image,
            @JsonProperty("created") final OffsetDateTime created,
            @JsonProperty("updated") final OffsetDateTime updated,
            @JsonProperty("description") final String description,
            @JsonProperty("content") final String content
    ) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.created = created;
        this.updated = updated;
        this.description = description;
        this.content = Jsoup.clean(content, WHITELIST);
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
