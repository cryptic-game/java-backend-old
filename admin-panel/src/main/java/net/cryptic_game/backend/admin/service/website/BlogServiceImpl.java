package net.cryptic_game.backend.admin.service.website;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.converter.website.BlogPostConverter;
import net.cryptic_game.backend.admin.converter.website.BlogPostIdConverter;
import net.cryptic_game.backend.admin.converter.website.BlogPostSmallConverter;
import net.cryptic_game.backend.admin.dto.website.BlogPost;
import net.cryptic_game.backend.admin.dto.website.BlogPost.Id;
import net.cryptic_game.backend.admin.dto.website.BlogPostSmall;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.model.website.BlogPostModel;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.cryptic_game.backend.admin.repository.website.BlogPostRepository;
import net.cryptic_game.backend.admin.repository.website.BlogPostSmallRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostRepository postRepository;
    private final BlogPostSmallRepository postSmallRepository;
    private final BlogPostConverter postConverter;
    private final BlogPostSmallConverter postSmallConverter;
    private final BlogPostIdConverter idConverter;

    @Override
    public Set<BlogPostSmall> findPosts(final String language) {
        return this.postSmallRepository.findAll(language)
                .stream()
                .map(this.postSmallConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<BlogPost> findPost(final Id id) {
        final IdModel idModel = this.idConverter.toModel(id);

        return this.postRepository.findById(idModel)
                .map(this.postConverter::toDto);
    }

    @Override
    public BlogPost savePost(final Id id, final BlogPost post) {
        final IdModel idModel = this.idConverter.toModel(id);
        final BlogPostModel model = this.postRepository.findById(idModel)
                .orElseThrow(() -> new NotFoundException(id.toString(), "POST_NOT_FOUND"));

        this.postConverter.override(model, post);
        model.setUpdated(OffsetDateTime.now());

        return this.postConverter.toDto(
                this.postRepository.save(model)
        );
    }

    @Override
    public BlogPost savePost(final BlogPost post) {
        final BlogPostModel model = this.postConverter.toModel(post);

        model.setCreated(OffsetDateTime.now());
        model.setUpdated(null);
        model.setPublished(false);

        return this.postConverter.toDto(
                this.postRepository.save(model)
        );
    }

    @Override
    public void deletePost(final Id id) {
        final IdModel idModel = this.idConverter.toModel(id);
        if (!this.postRepository.existsById(idModel)) {
            throw new NotFoundException(id.toString(), "POST_NOT_FOUND");
        }

        this.postRepository.deleteById(idModel);
    }
}
