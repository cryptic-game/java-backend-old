package net.cryptic_game.backend.admin.controller.website;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.cryptic_game.backend.admin.dto.website.TeamDepartment;
import net.cryptic_game.backend.admin.dto.website.TeamMember;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@Tag(name = "Team")
@RequestMapping("website/team")
public interface TeamController {

    @GetMapping
    Set<TeamMember> findMembers();

    @GetMapping("{memberId}")
    TeamMember findMember(@PathVariable("memberId") UUID memberId);

    @PostMapping
    @Secured("ROLE_WEBSITE")
    TeamMember postMember(@RequestBody @Valid TeamMember member);

    @PutMapping("{memberId}")
    @Secured("ROLE_WEBSITE")
    TeamMember putMember(@PathVariable("memberId") UUID memberId, @RequestBody @Valid TeamMember member);

    @DeleteMapping("{memberId}")
    @Secured("ROLE_WEBSITE")
    void deleteMember(@PathVariable("memberId") UUID memberId);

    @GetMapping("department")
    Set<TeamDepartment> findDepartments();

    @GetMapping("department/{departmentId}")
    TeamDepartment findDepartment(@PathVariable("departmentId") UUID departmentId);

    @PostMapping("department")
    @Secured("ROLE_WEBSITE")
    TeamDepartment postDepartment(@RequestBody @Valid TeamDepartment department);

    @PutMapping("department/{departmentId}")
    @Secured("ROLE_WEBSITE")
    TeamDepartment putDepartment(@PathVariable("departmentId") UUID departmentId, @RequestBody @Valid TeamDepartment department);

    @DeleteMapping("department/{departmentId}")
    @Secured("ROLE_WEBSITE")
    void deleteDepartment(@PathVariable("departmentId") UUID departmentId);
}
