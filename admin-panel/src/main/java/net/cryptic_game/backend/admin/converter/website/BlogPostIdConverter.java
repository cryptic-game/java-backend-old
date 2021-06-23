package net.cryptic_game.backend.admin.converter.website;

import net.cryptic_game.backend.dto.website.BlogPost.Id;
import net.cryptic_game.backend.admin.model.website.BlogPostModel.IdModel;
import net.getnova.framework.core.Converter;
import org.springframework.stereotype.Component;

@Component
public class BlogPostIdConverter implements Converter<IdModel, Id> {

    @Override
    public IdModel toModel(final Id dto) {
        return new IdModel(dto.getLanguage(), dto.getPostId());
    }

    @Override
    public Id toDto(final IdModel model) {
        return new Id(model.getLanguage(), model.getPostId());
    }

    @Override
    public void override(final IdModel model, final Id dto) {
        model.setLanguage(dto.getLanguage());
        model.setPostId(dto.getPostId());
    }

    @Override
    public void merge(final IdModel model, final Id dto) {
        throw new UnsupportedOperationException();
    }
}
