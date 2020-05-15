package net.cryptic_game.backend.data.user;

import com.google.gson.JsonObject;
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
import java.util.Objects;

/**
 * Entity representing an user settings entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "user_settings")
public class UserSetting extends TableModel implements JsonSerializable {

    @EmbeddedId
    private UserSettingKey key;

    @Column(name = "value", updatable = true, nullable = true, length = 2048)
    private String value;

    /**
     * Fetches a {@link UserSetting}
     *
     * @param user {@link User} of the {@link UserSetting}
     * @param key  Key of the {@link UserSetting}
     * @return Fetched {@link UserSetting}
     */
    public static UserSetting getSetting(final User user, final String key) {
        try (Session sqlSession = sqlConnection.openSession()) {
            List<UserSetting> settings = sqlSession.createQuery("select object(s) from UserSetting as s where s.key.user.id = :userId " +
                    "and s.key.key = :key", UserSetting.class)
                    .setParameter("userId", user.getId())
                    .setParameter("key", key)
                    .getResultList();
            if (settings.size() > 0) return settings.get(0);
            return null;
        }
    }

    /**
     * Fetches all {@link UserSetting}s of a {@link User}
     *
     * @param user {@link User} of the {@link UserSetting}
     * @return Fetched {@link UserSetting}s
     */
    public static List<UserSetting> getSettings(User user) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession.createQuery("select object(s) from UserSetting as s where s.key.user.id = :userId ", UserSetting.class)
                    .setParameter("userId", user.getId())
                    .getResultList();
        }
    }

    /**
     * Returns the current {@link User}
     *
     * @return the current {@link User}
     */
    public User getUser() {
        return this.key.user;
    }

    /**
     * Returns the key for the current user setting
     *
     * @return Key for the current user setting
     */
    public String getKey() {
        return this.key.key;
    }

    /**
     * Sets the {@link UserSettingKey} for the current user setting
     *
     * @param key The new {@link UserSettingKey}
     */
    public void setKey(final UserSettingKey key) {
        this.key = key;
    }

    /**
     * Returns the value for the current user setting
     *
     * @return value for the current user setting
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value for the current user setting
     *
     * @param value The new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link UserSetting} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("key", this.getKey())
                .add("value", this.getValue())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link UserSetting}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link UserSetting} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSetting that = (UserSetting) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getKey(), that.getKey()) &&
                Objects.equals(getValue(), that.getValue());
    }

    /**
     * Hashes the {@link UserSetting} using {@link Objects} hash method
     *
     * @return Hash of the {@link UserSetting}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getKey(), getValue());
    }

    /**
     * Key of the {@link UserSetting} entity
     */
    @SuppressWarnings("JpaDataSourceORMInspection")
    @Embeddable
    public static class UserSettingKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "user", updatable = false, nullable = false)
        @Type(type = "uuid-char")
        private User user;

        @Column(name = "settingKey", updatable = false, nullable = false)
        private String key;

        /**
         * Empty constructor to create a new {@link UserSettingKey}
         */
        public UserSettingKey() {
        }

        /**
         * Creates a new {@link UserSettingKey}
         *
         * @param user {@link User} of the {@link UserSettingKey}
         * @param key  Key of the {@link UserSettingKey}
         */
        public UserSettingKey(final User user, final String key) {
            this.user = user;
            this.key = key;
        }
    }
}
