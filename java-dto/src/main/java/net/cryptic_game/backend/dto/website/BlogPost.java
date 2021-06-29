package net.cryptic_game.backend.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.Set;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

@Data
@AllArgsConstructor
public class BlogPost {

    private static final String LANGUAGE_REGEX = "[a-z]{2}";
    private static final String POST_ID_REGEX = "[a-z-]+";
    private static final String IMAGE_REGEX = "https://cdn\\.cryptic-game\\.net/images/blog/(.+)\\.jpe?g";

    private static final Whitelist WHITELIST = Whitelist.relaxed()
            .addEnforcedAttribute("a", "target", "_blank")
            .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");

    private final Id id;
    private final String title;
    @Pattern(regexp = IMAGE_REGEX)
    private final String image;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final boolean published;
    private final String description;
    private final String content;
    private final Set<String> languages;

    public BlogPost(
            @JsonProperty("id") final Id id,
            @JsonProperty("title") final String title,
            @JsonProperty("image") final String image,
            @JsonProperty("created") final OffsetDateTime created,
            @JsonProperty("updated") final OffsetDateTime updated,
            @JsonProperty("published") final boolean published,
            @JsonProperty("description") final String description,
            @JsonProperty("content") final String content
    ) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.created = created;
        this.updated = updated;
        this.published = published;
        this.description = description;
        this.content = Jsoup.clean(content, WHITELIST);
        this.languages = null;
    }

    @Data
    public static final class Id {

        @Pattern(regexp = LANGUAGE_REGEX)
        private final String language; // TODO java.util.Locale;
        @Pattern(regexp = POST_ID_REGEX)
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
