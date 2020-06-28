package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Entity representing a chat channel entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_channel")
@Data
public class ChatChannel extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    /**
     * Creates a new {@link ChatChannel} with a given name.
     *
     * @param name the name of the channel
     * @return the newly generated channel
     */
    public static ChatChannel createChannel(final String name) {
        final ChatChannel channel = new ChatChannel();
        channel.setName(name);

        channel.saveOrUpdate();
        return channel;
    }

    /**
     * Deletes a {@link ChatChannel}.
     *
     * @param id the {@link UUID} of the {@link ChatChannel}
     */
    public static void removeChannel(final UUID id) {
        final ChatChannel channel = getById(id);
        if (channel != null) {
            channel.delete();
        }
    }

    /**
     * Returns a {@link ChatChannel} by it's UUID.
     *
     * @param id the {@link UUID} of the Channel
     * @return the {@link ChatChannel} which got the {@link UUID}
     */
    public static ChatChannel getById(final UUID id) {
        return getById(ChatChannel.class, id);
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatChannel} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .build();
    }
}
