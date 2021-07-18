package net.cryptic_game.backend.admin.service.website.impl;

import net.cryptic_game.backend.admin.converter.website.TeamMemberConverter;
import net.cryptic_game.backend.admin.model.website.TeamMemberModel;
import net.cryptic_game.backend.admin.repository.website.TeamMemberRepository;
import net.cryptic_game.backend.admin.service.website.TeamMemberService;
import net.cryptic_game.backend.dto.website.TeamMember;
import net.getnova.framework.core.service.AbstractCommonIdCrudService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TeamServiceImpl
        extends AbstractCommonIdCrudService<TeamMember, UUID, TeamMemberModel>
        implements TeamMemberService {

    public TeamServiceImpl(
            final TeamMemberRepository repository,
            final TeamMemberConverter converter
    ) {
        super("TEAM_MEMBER", repository, converter);
    }
}
