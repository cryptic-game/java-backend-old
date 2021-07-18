package net.cryptic_game.backend.admin.service.website.impl;

import net.cryptic_game.backend.admin.converter.website.TeamDepartmentConverter;
import net.cryptic_game.backend.admin.model.website.TeamDepartmentModel;
import net.cryptic_game.backend.admin.repository.website.TeamDepartmentRepository;
import net.cryptic_game.backend.admin.service.website.TeamDepartmentService;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import net.getnova.framework.core.service.AbstractCommonIdCrudService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TeamDepartmentServiceImpl
        extends AbstractCommonIdCrudService<TeamDepartment, UUID, TeamDepartmentModel>
        implements TeamDepartmentService {

    public TeamDepartmentServiceImpl(
            final TeamDepartmentRepository repository,
            final TeamDepartmentConverter converter
    ) {
        super("TEAM_DEPARTMENT", repository, converter);
    }
}
