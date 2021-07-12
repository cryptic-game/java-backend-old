package net.cryptic_game.backend.data.sql.entities.user;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModelAutoId;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.Constants;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Entity representing an user entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_user")
@Cacheable
public final class User extends TableModelAutoId {

    @Column(name = "username", unique = true, length = Constants.USERNAME_LENGTH)
    private String username;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    @Column(name = "last", nullable = false)
    private OffsetDateTime last;

    public JsonObject serializePublic() {
        return JsonBuilder.create("id", this.getId())
                .add("username", this.getUsername())
                .add("created", this.getCreated())
                .build();
    }

    public boolean isNewUser() {
        return this.username == null;
    }
}

