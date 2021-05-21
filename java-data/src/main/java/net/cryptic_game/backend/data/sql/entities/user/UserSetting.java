package net.cryptic_game.backend.data.sql.entities.user;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.getnova.framework.jpa.model.TableModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Entity representing an user settings entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_settings")
public final class UserSetting extends TableModel implements JsonSerializable {

    @EmbeddedId
    private UserSettingKey key;

    @Column(name = "value", updatable = true, nullable = true, length = 2048)
    private String value;

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
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSettingKey implements Serializable {

        @ManyToOne
        @JoinColumn(name = "user_id", updatable = false, nullable = false)
        private User user;

        @Column(name = "settingKey", updatable = false, nullable = false)
        private String key;
    }
}
