package net.cryptic_game.backend.admin.converter.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.dto.website.BlogPost;
import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.getnova.framework.core.Converter;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogPostConverter implements Converter<BlogPostModel, BlogPost> {

    private final BlogPostIdConverter idConverter;

    @Override
    public BlogPostModel toModel(final BlogPost dto) {
        return new BlogPostModel(
                this.idConverter.toModel(dto.getId()),
                dto.getTitle(),
                dto.getImage(),
                dto.getCreated(),
                dto.getUpdated(),
                dto.getDescription(),
                dto.getContent()
        );
    }

    @Override
    public BlogPost toDto(final BlogPostModel model) {
        return new BlogPost(
                this.idConverter.toDto(model.getId()),
                model.getTitle(),
                model.getImage(),
                model.getCreated(),
                model.getUpdated(),
                model.getDescription(),
                model.getContent()
        );
    }

    @Override
    public void override(final BlogPostModel model, final BlogPost dto) {
        this.idConverter.override(model.getId(), dto.getId());

        model.setTitle(dto.getTitle());
        model.setContent(dto.getContent());
        model.setDescription(dto.getDescription());
        model.setContent(dto.getContent());
    }

    @Override
    public void merge(final BlogPostModel model, final BlogPost dto) {
        throw new NotImplementedException();
    }
}
