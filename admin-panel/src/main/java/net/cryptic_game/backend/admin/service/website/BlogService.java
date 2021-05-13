package net.cryptic_game.backend.admin.service.website;

import net.cryptic_game.backend.admin.dto.website.BlogPost;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;
import net.cryptic_game.backend.admin.dto.website.BlogPostSmall;

import java.util.Optional;
import java.util.Set;

public interface BlogService {

    Set<BlogPostSmall> findPosts(String language);

    Optional<BlogPost> findPost(Id id);

    BlogPost savePost(Id id, BlogPost post);

    BlogPost savePost(BlogPost post);

    void deletePost(Id id);
}
