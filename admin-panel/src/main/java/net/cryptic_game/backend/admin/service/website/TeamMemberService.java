package net.cryptic_game.backend.admin.service.website;

import java.util.UUID;

import net.cryptic_game.backend.dto.website.TeamMember;
import net.getnova.framework.core.service.CrudService;

public interface TeamMemberService extends CrudService<TeamMember, UUID> {
}
