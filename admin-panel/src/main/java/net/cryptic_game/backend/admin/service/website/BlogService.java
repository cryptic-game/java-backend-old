package net.cryptic_game.backend.admin.service.website;

import net.cryptic_game.backend.dto.website.BlogPost;
import net.cryptic_game.backend.dto.website.BlogPost.Id;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import de.m4rc3l.nova.core.service.SmallCrudService;

import java.util.List;

public interface BlogService extends SmallCrudService<BlogPost, BlogPostSmall, Id> {

    List<BlogPostSmall> findPosts(String language);
}
