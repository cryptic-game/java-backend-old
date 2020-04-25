package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a chat channel entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_channel")
public class ChatChannel extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    /**
     * Creates a new {@link ChatChannel} with a given name
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
     * Deletes a {@link ChatChannel}
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
     * Returns a {@link ChatChannel} by it's UUID
     *
     * @param id the {@link UUID} of the Channel
     * @return the {@link ChatChannel} which got the {@link UUID}
     */
    public static ChatChannel getById(final UUID id) {
        return getById(ChatChannel.class, id);
    }

    /**
     * Returns the name of the {@link ChatChannel}
     *
     * @return the name of the {@link ChatChannel}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for the {@link ChatChannel}
     *
     * @param name new Name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Compares an {@link Object} if it equals the {@link ChatChannel}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link ChatChannel} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ChatChannel channel = (ChatChannel) o;
        return Objects.equals(getName(), channel.getName()) &&
                Objects.equals(getId(), channel.getId());
    }

    /**
     * Hashes the {@link ChatChannel} using {@link Objects} hash method
     *
     * @return Hash of the {@link ChatChannel}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatChannel} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .build();
    }
}
