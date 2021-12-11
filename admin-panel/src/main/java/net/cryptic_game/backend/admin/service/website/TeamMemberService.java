package net.cryptic_game.backend.admin.service.website;

import net.cryptic_game.backend.dto.website.TeamMember;
import de.m4rc3l.nova.core.service.CrudService;

import java.util.UUID;

public interface TeamMemberService extends CrudService<TeamMember, UUID> {
}
