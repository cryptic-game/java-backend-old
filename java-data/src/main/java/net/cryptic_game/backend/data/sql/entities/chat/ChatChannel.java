package net.cryptic_game.backend.data.sql.entities.chat;

import lombok.Data;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity representing a chat channel entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_channel")
@Data
public final class ChatChannel extends TableModelAutoId {

    public static final int MAX_NAME_LENGTH = 32;

    @Column(name = "name", updatable = true, nullable = false, length = MAX_NAME_LENGTH)
    private String name;

    /**
     * @param session the sql {@link Session} with transaction
     *                Deletes also the {@link ChatMessage}s and the {@link ChatChannelAccess}s from that channel.
     */
    public void delete(final Session session) {
        session.createQuery("delete from ChatMessage as m where m.channel = :channel")
                .setParameter("channel", this)
                .executeUpdate();
        session.createQuery("delete from ChatChannelAccess as ca where ca.channel = :channel")
                .setParameter("channel", this)
                .executeUpdate();
// TODO: FIX ME
    }
}
