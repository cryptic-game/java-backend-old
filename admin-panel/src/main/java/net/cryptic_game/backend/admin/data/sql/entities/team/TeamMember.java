package net.cryptic_game.backend.admin.data.sql.entities.team;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
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
public final class TeamMember extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @Column(name = "github_id", updatable = true, nullable = false, unique = true)
    private long githubId;

    @ManyToOne
    @Type(type = "uuid-char")
    @JoinColumn(name = "department", updatable = true, nullable = false)
    private TeamDepartment department;

    @Column(name = "joined", updatable = true, nullable = false)
    private OffsetDateTime joined;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("github_id", this.getGithubId())
                .add("department_id", this.getDepartment().getId())
                .add("joined", this.getJoined())
                .build();
    }
}
