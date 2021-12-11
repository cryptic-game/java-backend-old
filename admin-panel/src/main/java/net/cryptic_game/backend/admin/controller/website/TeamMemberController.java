package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.dto.website.TeamMember;
import de.m4rc3l.nova.core.controller.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Team")
@RequestMapping("website/team")
public interface TeamMemberController extends CrudController<TeamMember, UUID> {
}
