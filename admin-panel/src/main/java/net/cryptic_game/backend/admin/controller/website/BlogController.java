package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import net.cryptic_game.backend.dto.website.BlogPost;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Blog")
@RequestMapping("website/blog")
public interface BlogController {

    @GetMapping("{language}")
    List<BlogPostSmall> findPosts(@PathVariable("language") String language);

    @GetMapping("{language}/{postId}")
    BlogPost findPost(@PathVariable("language") String language, @PathVariable("postId") String postId);

    @PostMapping
    BlogPost postPost(@RequestBody BlogPost post);

    @PutMapping("{language}/{postId}")
    BlogPost putPost(@PathVariable("language") String language, @PathVariable("postId") String postId, @RequestBody BlogPost post);

    @DeleteMapping("{language}/{postId}")
    void deletePost(@PathVariable("language") String language, @PathVariable("postId") String postId);
}
