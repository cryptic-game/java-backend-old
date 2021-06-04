package net.cryptic_game.backend.data.sql.entities.user;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModelAutoId;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Entity representing an user suspension entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_suspension")
public final class UserSuspension extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

//    TODO
//    @ManyToOne
//    @JoinColumn(name = "admin_user_id", updatable = false, nullable = false)
//    @Type(type = "uuid-char")
//    private AdminUser adminUser;

    @Column(name = "timestamp", updatable = false, nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "expires", updatable = true, nullable = false)
    private OffsetDateTime expires;

    @Column(name = "reason", updatable = true, nullable = true)
    private String reason;

    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user_id", this.getUser().getId())
//                TODO
//                .add("admin_user_id", this.getAdminUser().getId())
                .add("timestamp", this.getTimestamp())
                .add("expires", this.getExpires())
                .add("reason", !this.getReason().isEmpty(), this::getReason)
                .build();
    }
}
