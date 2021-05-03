package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Set;
import javax.validation.Valid;

import net.cryptic_game.backend.admin.dto.website.BlogPost;
import net.cryptic_game.backend.admin.dto.website.BlogPostSmall;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Team")
@RequestMapping("website/blog")
public interface BlogController {

    @GetMapping("{language}")
    Set<BlogPostSmall> findPosts(@PathVariable("language") String language);

    @GetMapping("{language}/{postId}")
    BlogPost findPost(@PathVariable("language") String language, @PathVariable("postId") String postId);

    @PostMapping
    @Secured("ROLE_WEBSITE")
    BlogPost postPost(@RequestBody @Valid BlogPost post);

    @PutMapping("{language}/{postId}")
    @Secured("ROLE_WEBSITE")
    BlogPost putPost(@PathVariable("language") String language, @PathVariable("postId") String postId, @RequestBody @Valid BlogPost post);

    @DeleteMapping("{language}/{postId}")
    @Secured("ROLE_WEBSITE")
    void deletePost(@PathVariable("language") String language, @PathVariable("postId") String postId);
}
