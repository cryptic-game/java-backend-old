package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import de.m4rc3l.nova.core.controller.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Team")
@RequestMapping("website/team/department")
public interface TeamDepartmentController extends CrudController<TeamDepartment, UUID> {
}
