package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import net.getnova.framework.core.controller.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Tag(name = "Team")
@RequestMapping("website/team/department")
public interface TeamDepartmentController extends CrudController<TeamDepartment, UUID> {
}
