package net.cryptic_game.backend.admin.service.website;

import net.cryptic_game.backend.dto.website.BlogPost;
import net.cryptic_game.backend.dto.website.BlogPost.Id;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import net.getnova.framework.core.service.SmallCrudService;

import java.util.List;

public interface BlogService extends SmallCrudService<BlogPost, BlogPostSmall, Id> {

    List<BlogPostSmall> findPosts(String language);
}
