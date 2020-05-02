package net.cryptic_game.backend.data.user;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing an user settings entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "user_settings")
public class UserSettings extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "user", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "settingValue", updatable = true, nullable = true)
    private String settingValue;

    /**
     * Returns the current {@link User}
     *
     * @return the current {@link User}
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the current {@link User}
     *
     * @param user the new {@link User}
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Returns the SettingValue for the current user setting
     *
     * @return SettingValue for the current user setting
     */
    public String getSettingValue() {
        return settingValue;
    }

    /**
     * Sets the SettingValue for the current user setting
     *
     * @param settingValue the new SettingValue
     */
    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link UserSettings} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user", this.getUser().getId())
                .add("settingValue", this.getSettingValue())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link UserSettings}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link UserSettings} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getSettingValue(), that.getSettingValue());
    }

    /**
     * Hashes the {@link UserSettings} using {@link Objects} hash method
     *
     * @return Hash of the {@link UserSettings}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getSettingValue());
    }
}
