package net.cryptic_game.backend.admin.endpoints.website_management.faq;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.admin.authentication.Permission;
import net.cryptic_game.backend.admin.data.sql.entities.website_management.faq.FAQEntry;
import net.cryptic_game.backend.admin.data.sql.repositories.website_management.faq.FAQEntryRepository;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "website_management/faq", description = "manages faq", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public class FAQEntryEndpoints {

    private final FAQEntryRepository faqEntryRepository;

    @ApiEndpoint(id = "change_answer", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse changeAnswer(@ApiParameter(id = "element_id") final UUID elementId,
                                    @ApiParameter(id = "new_answer") final String newAnswer) {
        final FAQEntry entry = this.faqEntryRepository.findById(elementId).orElse(null);
        if (entry == null) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_SUCH_ELEMENT");
        }
        if (checkXXS(newAnswer)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_HTML_TAGS_ALLOWED");
        }
        entry.setAnswer(newAnswer);
        return new ApiResponse(HttpResponseStatus.OK, this.faqEntryRepository.saveAndFlush(entry));
    }

    @ApiEndpoint(id = "create", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse createAnswer(@ApiParameter(id = "question") final String question,
                                    @ApiParameter(id = "answer") final String answer) {
        if (question == null || answer == null) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "PARAMETERS_CANNOT_BE_NULL");
        }

        if (this.faqEntryRepository.getByQuestion(question).isPresent()) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "QUESTION_ALREADY_EXISTS");
        }

        if (this.checkXXS(question) || this.checkXXS(answer)) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_HTML_TAGS_ALLOWED");
        }

        final FAQEntry faqEntry = new FAQEntry(question, answer);
        return new ApiResponse(HttpResponseStatus.OK, this.faqEntryRepository.saveAndFlush(faqEntry));
    }

    @ApiEndpoint(id = "delete", authentication = Permission.FAQ_MANAGEMENT)
    public ApiResponse deleteEntry(@ApiParameter(id = "element_id") final UUID elementId) {
        final Optional<FAQEntry> faqEntryOpt = this.faqEntryRepository.findById(elementId);
        if (faqEntryOpt.isPresent()) {
            this.faqEntryRepository.delete(faqEntryOpt.get());
            return new ApiResponse(HttpResponseStatus.OK);
        } else {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_SUCH_ELEMENT");
        }
    }

    @ApiEndpoint(id = "list")
    public ApiResponse list() {
        final List<FAQEntry> faqEntryList = this.faqEntryRepository.findAll();
        return new ApiResponse(HttpResponseStatus.BAD_REQUEST, faqEntryList);
    }

    /**
     * returns true if an xxs attempt might be there.
     *
     * @param content content to prove
     * @return true if html tags might be in the content
     */
    private boolean checkXXS(final String content) {
        //RegEx to find everything with < and no space or minus after it and > with not space or minus before
        return Pattern.compile("<[^- ]|[^- ]>").matcher(content).find();
    }
}
