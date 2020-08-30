package net.cryptic_game.backend.data.entities.chat;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.entities.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a chat channel access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_channel_access")
@Data
@Slf4j
public final class ChatChannelAccess extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private ChatChannel channel;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatChannelAccess} information.
     *
     * @return The generated {@link JsonObject}
     */
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user_id", this.getUser().getId())
                .add("channel_id", this.getChannel().getId())
                .build();
    }
}

