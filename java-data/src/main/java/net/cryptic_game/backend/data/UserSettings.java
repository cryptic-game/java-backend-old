package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_settings")
public class UserSettings extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "user", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @Column(name = "settingValue", updatable = true, nullable = true)
    private String settingValue;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("user", this.getUser().getId())
                .add("settingValue", this.getSettingValue())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSettings that = (UserSettings) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getSettingValue(), that.getSettingValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getSettingValue());
    }
}
