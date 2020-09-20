package net.cryptic_game.backend.data.sql.entities.user;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import net.cryptic_game.backend.data.Constants;
import org.hibernate.Session;

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

    @Column(name = "username", updatable = true, nullable = false, unique = true, length = Constants.USERNAME_LENGTH)
    private String username;

    @JsonTransient
    @Column(name = "password", updatable = true, nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    @Column(name = "last", updatable = true, nullable = false)
    private OffsetDateTime last;

    /**
     * Create a {@link User} with the given user data.
     *
     * @param session  the sql {@link Session} with transaction
     * @param username The username of the new user
     * @param password The password of the new user
     * @return The instance of the created {@link User}
     */
    public static User createUser(final Session session, final String username, final String password) {
        final OffsetDateTime now = OffsetDateTime.now();

        final User user = new User();
        user.setUsername(username);
        user.setCreated(now);
        user.setLast(now);
        user.setPassword(password);

        // TODO: FIXME
        return user;
    }

    /**
     * Set a new password for the given {@link User}.
     *
     * @param newPassword New password to be set
     */
    public void setPassword(final String newPassword) {
        this.setPasswordHash(SecurityUtils.hash(newPassword));
    }

    /**
     * Checks whether a password matches the current password of the {@link User}.
     *
     * @param input The password to be checked
     * @return true if the password is correct | false if the password is wrong
     */
    public boolean verifyPassword(final String input) {
        return SecurityUtils.verify(input, this.getPasswordHash());
    }

    public JsonObject serializePublic() {
        return JsonBuilder.create("id", this.getId())
                .add("username", this.getUsername())
                .add("created", this.getCreated())
                .build();
    }
}

