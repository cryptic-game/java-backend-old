package net.cryptic_game.backend.dto.website;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.getnova.framework.core.Validatable;
import net.getnova.framework.core.exception.ValidationException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class BlogPost implements Validatable {

    private static final String LANGUAGE_REGEX = "[a-z]{2}";
    private static final String POST_ID_REGEX = "[a-z-]+";
    private static final String IMAGE_REGEX = "https://cdn\\.cryptic-game\\.net/images/blog/(.+)\\.jpe?g";

    private static final Whitelist WHITELIST = Whitelist.relaxed()
            .addEnforcedAttribute("a", "target", "_blank")
            .addEnforcedAttribute("a", "rel", "nofollow noopener noreferrer");

    private static final OutputSettings OUTPUT_SETTINGS = new OutputSettings()
            .prettyPrint(false);

    private final Id id;
    private final String title;
    private final String image;
    private final OffsetDateTime created;
    private final OffsetDateTime updated;
    private final Boolean published;
    private final String description;
    private final String content;
    private final Set<String> languages;

    public BlogPost(
            @JsonProperty("id") final Id id,
            @JsonProperty("title") final String title,
            @JsonProperty("image") final String image,
            @JsonProperty("created") final OffsetDateTime created,
            @JsonProperty("updated") final OffsetDateTime updated,
            @JsonProperty("published") final Boolean published,
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
        this.content = Jsoup.clean(content, "", WHITELIST, OUTPUT_SETTINGS);
        this.languages = null;
    }

    @Override
    public void validate() throws ValidationException {
        if (this.id == null) {
            throw new ValidationException("id", "NOT_NULL");
        }

        this.id.validate();

        if (this.title == null || this.title.isBlank()) {
            throw new ValidationException("title", "NOT_BLANK");
        }

        if (this.image == null) {
            throw new ValidationException("image", "NOT_NULL");
        }

        if (IMAGE_REGEX.matches(this.image)) {
            throw new ValidationException("image", "MATCH_REGEX");
        }

        if (this.published == null) {
            throw new ValidationException("published", "NOT_NULL");
        }

        if (this.description == null || this.description.isBlank()) {
            throw new ValidationException("description", "NOT_BLANK");
        }
    }

    @Data
    public static final class Id implements Validatable {

        private final String language; // TODO java.util.Locale;
        private final String postId;

        public Id(
                @JsonProperty("language") final String language,
                @JsonProperty("postId") final String postId
        ) {
            this.language = language;
            this.postId = postId;
        }

        @Override
        public void validate() throws ValidationException {
            if (this.language == null) {
                throw new ValidationException("language", "NOT_NULL");
            }

            if (LANGUAGE_REGEX.matches(this.language)) {
                throw new ValidationException("language", "MATCH_REGEX");
            }

            if (this.postId == null) {
                throw new ValidationException("postId", "NOT_NULL");
            }

            if (POST_ID_REGEX.matches(this.postId)) {
                throw new ValidationException("postId", "MATCH_REGEX");
            }
        }
    }
}
