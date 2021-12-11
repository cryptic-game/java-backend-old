package net.cryptic_game.backend.admin.converter.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.cryptic_game.backend.admin.repository.website.BlogPostRepository;
import net.cryptic_game.backend.dto.website.BlogPost;
import de.m4rc3l.nova.core.Converter;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogPostConverter implements Converter<BlogPostModel, BlogPost> {

    private final BlogPostRepository blogPostRepository;
    private final BlogPostIdConverter idConverter;

    @Override
    public BlogPostModel toModel(final BlogPost dto) {
        return new BlogPostModel(
                dto.getId() == null ? null : this.idConverter.toModel(dto.getId()),
                dto.getTitle(),
                dto.getImage(),
                dto.getCreated(),
                dto.getUpdated(),
                dto.getPublished(),
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
                model.isPublished(),
                model.getDescription(),
                model.getContent(),
                this.blogPostRepository.findLanguagesById(model.getId().getPostId())
        );
    }

    @Override
    public void override(final BlogPostModel model, final BlogPost dto) {
        if (dto.getId() != null) {
            this.idConverter.override(model.getId(), dto.getId());
        }

        model.setTitle(dto.getTitle());
        model.setImage(dto.getImage());
        model.setContent(dto.getContent());
        model.setPublished(dto.getPublished());
        model.setDescription(dto.getDescription());
        model.setContent(dto.getContent());
    }

    @Override
    public void merge(final BlogPostModel model, final BlogPost dto) {
        throw new NotImplementedException();
    }
}
