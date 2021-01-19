package net.cryptic_game.backend.admin.endpoints.website.faq;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.admin.authentication.Permission;
import net.cryptic_game.backend.admin.data.sql.entities.website_management.faq.FaqEntry;
import net.cryptic_game.backend.admin.data.sql.repositories.website_management.faq.FaqEntryRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "website/faq", description = "manages faq", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public class FaqEntryEndpoints {

    private static final Pattern XXS_FILTER = Pattern.compile("<[^ ]+>");

    private final FaqEntryRepository faqEntryRepository;

    @ApiEndpoint(id = "list")
    public ApiResponse list() {
        return new ApiResponse(HttpResponseStatus.BAD_REQUEST, this.faqEntryRepository.findAll());
    }

    @ApiEndpoint(id = "add", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse add(@ApiParameter(id = "question") final String question,
                           @ApiParameter(id = "answer") final String answer) {
        if (checkXxs(question) || checkXxs(answer)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_HTML_TAGS_ALLOWED");
        }

        if (this.faqEntryRepository.getByQuestion(question).isPresent()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "QUESTION_ALREADY_EXISTS");
        }

        final FaqEntry faqEntry = new FaqEntry(question, answer);
        return new ApiResponse(HttpResponseStatus.OK, this.faqEntryRepository.save(faqEntry));
    }

    @ApiEndpoint(id = "update", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse update(@ApiParameter(id = "id") final UUID id,
                              @ApiParameter(id = "question") final String question,
                              @ApiParameter(id = "answer") final String answer) {
        if (checkXxs(question) || checkXxs(answer)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_HTML_TAGS_ALLOWED");
        }

        final Optional<FaqEntry> faqEntryOpt = this.faqEntryRepository.getByQuestion(question);
        if (faqEntryOpt.isEmpty()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_SUCH_ELEMENT");
        }

        final FaqEntry faqEntry = faqEntryOpt.get();
        faqEntry.setQuestion(question);
        faqEntry.setAnswer(answer);

        return new ApiResponse(HttpResponseStatus.OK, this.faqEntryRepository.save(faqEntry));
    }

    @ApiEndpoint(id = "delete", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse deleteEntry(@ApiParameter(id = "element_id") final UUID elementId) {
        final Optional<FaqEntry> faqEntryOpt = this.faqEntryRepository.findById(elementId);
        if (faqEntryOpt.isPresent()) {
            this.faqEntryRepository.delete(faqEntryOpt.get());
            return new ApiResponse(HttpResponseStatus.OK);
        } else {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_SUCH_ELEMENT");
        }
    }

    /**
     * returns true if an xxs attempt might be there.
     *
     * @param content content to prove
     * @return true if html tags might be in the content
     */
    private static boolean checkXxs(final String content) {
        return XXS_FILTER.matcher(content).find();
    }
}