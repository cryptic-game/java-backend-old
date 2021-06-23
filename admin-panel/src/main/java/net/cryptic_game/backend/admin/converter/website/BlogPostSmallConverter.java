package net.cryptic_game.backend.admin.converter.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import net.cryptic_game.backend.admin.model.website.BlogPostSmallModel;
import net.getnova.framework.core.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogPostSmallConverter implements Converter<BlogPostSmallModel, BlogPostSmall> {

    private final BlogPostIdConverter idConverter;

    @Override
    public BlogPostSmallModel toModel(final BlogPostSmall dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlogPostSmall toDto(final BlogPostSmallModel model) {
        return new BlogPostSmall(
                this.idConverter.toDto(model.getId()),
                model.getTitle(),
                model.getImage(),
                model.getCreated(),
                model.getUpdated(),
                model.isPublished(),
                model.getDescription()
        );
    }

    @Override
    public void override(final BlogPostSmallModel model, final BlogPostSmall dto) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void merge(final BlogPostSmallModel model, final BlogPostSmall dto) {
        throw new UnsupportedOperationException();
    }
}
