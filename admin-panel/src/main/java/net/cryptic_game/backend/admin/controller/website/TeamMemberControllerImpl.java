package net.cryptic_game.backend.admin.controller.website;

import net.cryptic_game.backend.admin.service.website.TeamMemberService;
import net.cryptic_game.backend.dto.website.TeamMember;
import net.getnova.framework.core.controller.AbstractCrudController;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TeamMemberControllerImpl
        extends AbstractCrudController<TeamMember, UUID>
        implements TeamMemberController {

    public TeamMemberControllerImpl(
            final TeamMemberService service
    ) {
        super(service);
    }
}
