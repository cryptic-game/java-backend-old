package net.cryptic_game.backend.admin.service.website.impl;

import net.cryptic_game.backend.admin.converter.website.BlogPostConverter;
import net.cryptic_game.backend.admin.converter.website.BlogPostIdConverter;
import net.cryptic_game.backend.admin.converter.website.BlogPostSmallConverter;
import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.cryptic_game.backend.admin.model.website.BlogPostSmallModel;
import net.cryptic_game.backend.admin.repository.website.BlogPostRepository;
import net.cryptic_game.backend.admin.repository.website.BlogPostSmallRepository;
import net.cryptic_game.backend.admin.service.website.BlogService;
import net.cryptic_game.backend.dto.website.BlogPost;
import net.cryptic_game.backend.dto.website.BlogPost.Id;
import net.cryptic_game.backend.dto.website.BlogPostSmall;
import net.getnova.framework.core.exception.NotFoundException;
import net.getnova.framework.core.service.AbstractSmallIdCrudService;
import net.getnova.framework.core.utils.ValidationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl
        extends AbstractSmallIdCrudService<BlogPost, BlogPostSmall, Id, BlogPostModel, BlogPostSmallModel, IdModel>
        implements BlogService {

    public BlogServiceImpl(
            final BlogPostRepository repository,
            final BlogPostSmallRepository smallRepository,
            final BlogPostConverter converter,
            final BlogPostSmallConverter smallConverter,
            final BlogPostIdConverter idConverter
    ) {
        super("BLOG_POST", repository, smallRepository, converter, smallConverter, idConverter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogPostSmall> findPosts(final String language) {
        return ((BlogPostSmallRepository) this.smallRepository).findAll(language)
                .stream()
                .map(this.smallConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BlogPost save(final Id id, final BlogPost dto) {
        ValidationUtils.validate(dto);

        final IdModel idModel = this.idConverter.toModel(id);

        final BlogPostModel model = this.repository.findById(idModel)
                .orElseThrow(() -> new NotFoundException(this.name));

        final boolean published = model.isPublished();

        this.converter.override(model, dto);

        if (published) {
            model.setUpdated(OffsetDateTime.now());
        }

        return this.converter.toDto(model);
    }

    @Override
    public BlogPost save(final BlogPost dto) {
        return super.save(new BlogPost(
                dto.getId(),
                dto.getTitle(),
                dto.getImage(),
                OffsetDateTime.now(),
                null,
                false,
                dto.getDescription(),
                dto.getContent(),
                dto.getLanguages()
        ));
    }
}
