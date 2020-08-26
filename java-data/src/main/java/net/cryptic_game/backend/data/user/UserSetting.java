package net.cryptic_game.backend.data.user;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModel;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * Entity representing an user settings entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "user_settings")
@Data
public final class UserSetting extends TableModel implements JsonSerializable {

    @EmbeddedId
    private UserSettingKey key;

    @Column(name = "value", updatable = true, nullable = true, length = 2048)
    private String value;

    /**
     * Fetches a {@link UserSetting}.
     *
     * @param session the sql {@link Session}
     * @param user {@link User} of the {@link UserSetting}
     * @param key  Key of the {@link UserSetting}
     * @return Fetched {@link UserSetting}
     */
    public static UserSetting getSetting(final Session session, final User user, final String key) {
        List<UserSetting> settings = session.createQuery("select object(s) from UserSetting as s where s.key.user = :user "
                + "and s.key.key = :key", UserSetting.class)
                .setParameter("user", user)
                .setParameter("key", key)
                .getResultList();
        if (settings.size() > 0) return settings.get(0);
        return null;
    }

    /**
     * Fetches all {@link UserSetting}s of a {@link User}.
     *
     * @param session the sql {@link Session}
     * @param user {@link User} of the {@link UserSetting}
     * @return Fetched {@link UserSetting}s
     */
    public static List<UserSetting> getSettings(final Session session, final User user) {
        return session.createQuery("select object(s) from UserSetting as s where s.key.user = :user ", UserSetting.class)
                .setParameter("user", user)
                .getResultList();
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link UserSetting} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("key", this.getKey().getKey())
                .add("value", this.getValue())
                .build();
    }

    /**
     * Key of the {@link UserSetting} entity.
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSettingKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "user", updatable = false, nullable = false)
        @Type(type = "uuid-char")
        private User user;

        @Column(name = "settingKey", updatable = false, nullable = false)
        private String key;
    }
}
