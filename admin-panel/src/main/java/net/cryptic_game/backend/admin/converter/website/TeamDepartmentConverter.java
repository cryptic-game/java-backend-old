package net.cryptic_game.backend.admin.converter.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import net.cryptic_game.backend.admin.model.website.TeamDepartmentModel;
import net.getnova.framework.core.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamDepartmentConverter implements Converter<TeamDepartmentModel, TeamDepartment> {

    @Override
    public TeamDepartmentModel toModel(final TeamDepartment dto) {
        return new TeamDepartmentModel(
                dto.getName(),
                dto.getDescription()
        );
    }

    @Override
    public TeamDepartment toDto(final TeamDepartmentModel model) {
        return new TeamDepartment(
                model.getId(),
                model.getName(),
                model.getDescription()
        );
    }

    @Override
    public void override(final TeamDepartmentModel model, final TeamDepartment dto) {
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
    }

    @Override
    public void merge(final TeamDepartmentModel model, final TeamDepartment dto) {
        if (dto.getName() != null) {
            model.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            model.setDescription(dto.getDescription());
        }
    }
}
