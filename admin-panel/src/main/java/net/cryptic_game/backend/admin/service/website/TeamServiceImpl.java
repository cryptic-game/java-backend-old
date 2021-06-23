package net.cryptic_game.backend.admin.service.website;

import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.converter.website.TeamDepartmentConverter;
import net.cryptic_game.backend.admin.converter.website.TeamMemberConverter;
import net.cryptic_game.backend.dto.website.TeamDepartment;
import net.cryptic_game.backend.dto.website.TeamMember;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import net.cryptic_game.backend.admin.model.website.TeamDepartmentModel;
import net.cryptic_game.backend.admin.model.website.TeamMemberModel;
import net.cryptic_game.backend.admin.repository.website.TeamDepartmentRepository;
import net.cryptic_game.backend.admin.repository.website.TeamMemberRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamMemberRepository memberRepository;
    private final TeamDepartmentRepository departmentRepository;
    private final TeamMemberConverter memberConverter;
    private final TeamDepartmentConverter departmentConverter;

    @Override
    public Set<TeamMember> findMembers() {
        return this.memberRepository.findAll()
                .stream()
                .map(this.memberConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<TeamMember> findMember(final UUID id) {
        return this.memberRepository.findById(id)
                .map(this.memberConverter::toDto);
    }

    @Override
    public TeamMember saveMember(final UUID id, final TeamMember member) {
        final TeamMemberModel model = this.memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "MEMBER_NOT_FOUND"));

        this.memberConverter.merge(model, member);

        return this.memberConverter.toDto(
                this.memberRepository.save(model)
        );
    }

    @Override
    public TeamMember saveMember(final TeamMember member) {
        return this.memberConverter.toDto(
                this.memberRepository.save(
                        this.memberConverter.toModel(member)
                )
        );
    }

    @Override
    public void deleteMember(final UUID id) {
        if (!this.memberRepository.existsById(id)) {
            throw new NotFoundException(id, "MEMBER_NOT_FOUND");
        }

        this.memberRepository.deleteById(id);
    }

    @Override
    public Set<TeamDepartment> findDepartments() {
        return this.departmentRepository.findAll()
                .stream()
                .map(this.departmentConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<TeamDepartment> findDepartment(final UUID id) {
        return this.departmentRepository.findById(id)
                .map(this.departmentConverter::toDto);
    }

    @Override
    public TeamDepartment saveDepartment(final UUID id, final TeamDepartment department) {
        final TeamDepartmentModel model = this.departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "DEPARTMENT_NOT_FOUND"));

        this.departmentConverter.merge(model, department);

        return this.departmentConverter.toDto(
                this.departmentRepository.save(model)
        );
    }

    @Override
    public TeamDepartment saveDepartment(final TeamDepartment department) {
        return this.departmentConverter.toDto(
                this.departmentRepository.save(
                        this.departmentConverter.toModel(department)
                )
        );
    }

    @Override
    public void deleteDepartment(final UUID id) {
        if (!this.departmentRepository.existsById(id)) {
            throw new NotFoundException(id, "DEPARTMENT_NOT_FOUND");
        }

        this.departmentRepository.deleteById(id);
    }
}
