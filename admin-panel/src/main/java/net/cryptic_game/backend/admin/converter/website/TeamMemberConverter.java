package net.cryptic_game.backend.admin.converter.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.dto.website.TeamMember;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.model.website.TeamDepartmentModel;
import net.cryptic_game.backend.admin.model.website.TeamMemberModel;
import net.cryptic_game.backend.admin.repository.website.TeamDepartmentRepository;
import net.getnova.framework.core.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberConverter implements Converter<TeamMemberModel, TeamMember> {

    private final TeamDepartmentRepository departmentRepository;

    @Override
    public TeamMemberModel toModel(final TeamMember dto) {
        final TeamDepartmentModel department = this.departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(dto.getDepartmentId(), "DEPARTMENT_NOT_FOUND"));

        return new TeamMemberModel(
                dto.getName(),
                dto.getGithubId(),
                department,
                dto.getJoined()
        );
    }

    @Override
    public TeamMember toDto(final TeamMemberModel model) {
        return new TeamMember(
                model.getId(),
                model.getName(),
                model.getGithubId(),
                model.getDepartment().getId(),
                model.getJoined()
        );
    }

    @Override
    public void override(final TeamMemberModel model, final TeamMember dto) {
        final TeamDepartmentModel department = this.departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(dto.getDepartmentId(), "DEPARTMENT_NOT_FOUND"));

        model.setName(dto.getName());
        model.setGithubId(dto.getGithubId());
        model.setDepartment(department);
        model.setJoined(dto.getJoined());
    }

    @Override
    public void merge(final TeamMemberModel model, final TeamMember dto) {
        if (dto.getName() != null) {
            model.setName(dto.getName());
        }

        if (dto.getGithubId() != null) {
            model.setGithubId(dto.getGithubId());
        }

        if (dto.getDepartmentId() != null) {
            final TeamDepartmentModel department = this.departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException(dto.getDepartmentId(), "DEPARTMENT_NOT_FOUND"));
            model.setDepartment(department);
        }

        if (dto.getJoined() != null) {
            model.setJoined(dto.getJoined());
        }
    }
}
