package net.cryptic_game.backend.admin.model.website;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.getnova.framework.jpa.model.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin_team_member")
public final class TeamMemberModel extends TableModelAutoId {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "github_id", nullable = false, unique = true)
    private long githubId;

    @ManyToOne
    @Type(type = "uuid-char")
    @JoinColumn(name = "department", nullable = false)
    private TeamDepartmentModel department;

    @Column(name = "joined", nullable = false)
    private OffsetDateTime joined;
}
