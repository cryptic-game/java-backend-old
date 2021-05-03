package net.cryptic_game.backend.admin.controller.website;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.dto.website.BlogPost;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;
import net.cryptic_game.backend.admin.dto.website.BlogPostSmall;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.service.website.BlogService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlogControllerImpl implements BlogController {

    private final BlogService blogService;

    @Override
    public Set<BlogPostSmall> findPosts(final String language) {
        return this.blogService.findPosts(language);
    }

    @Override
    public BlogPost findPost(final String language, final String postId) {
        final Id id = new Id(language, postId);
        return this.blogService.findPost(id)
                .orElseThrow(() -> new NotFoundException(id.toString(), "POST_NOT_FOUND"));
    }

    @Override
    public BlogPost postPost(final BlogPost post) {
        return this.blogService.savePost(post);
    }

    @Override
    public BlogPost putPost(final String language, final String postId, final BlogPost post) {
        return this.blogService.savePost(new Id(language, postId), post);
    }

    @Override
    public void deletePost(final String language, final String postId) {
        this.blogService.deletePost(new Id(language, postId));
    }
}
