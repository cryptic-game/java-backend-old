package net.cryptic_game.backend.admin.controller.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.service.website.BlogService;
import net.cryptic_game.backend.dto.website.BlogPost;
import net.cryptic_game.backend.dto.website.BlogPost.Id;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogControllerImpl implements BlogController {

    private final BlogService blogService;

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostSmall> findPosts(final String language) {
        return this.blogService.findPosts(language);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogPost findPost(final String language, final String postId) {
        return this.blogService.findById(new Id(language, postId));
    }

    @Override
    @Transactional
    public BlogPost postPost(final BlogPost post) {
        return this.blogService.save(post);
    }

    @Override
    @Transactional
    public BlogPost putPost(final String language, final String postId, final BlogPost post) {
        return this.blogService.save(new Id(language, postId), post);
    }

    @Override
    @Transactional
    public void deletePost(final String language, final String postId) {
        this.blogService.delete(new Id(language, postId));
    }
}
