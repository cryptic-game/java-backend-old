package net.cryptic_game.backend.admin.controller.website;

import net.cryptic_game.backend.admin.service.website.TeamDepartmentService;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import de.m4rc3l.nova.core.controller.AbstractCrudController;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TeamDepartmentControllerImpl
        extends AbstractCrudController<TeamDepartment, UUID>
        implements TeamDepartmentController {

    public TeamDepartmentControllerImpl(
            final TeamDepartmentService service
    ) {
        super(service);
    }
}
