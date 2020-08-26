package net.cryptic_game.backend.data.chat;

import lombok.Data;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.UUID;

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
     * Creates a new {@link ChatChannel} with a given name.
     *
     * @param session the sql {@link Session} with transaction
     * @param name    the name of the channel
     * @return the newly generated channel
     */
    public static ChatChannel createChannel(final Session session, final String name) {
        final ChatChannel channel = new ChatChannel();
        channel.setName(name);

        channel.saveOrUpdate(session);
        return channel;
    }

    /**
     * Returns a {@link ChatChannel} by it's UUID.
     *
     * @param session the sql {@link Session}
     * @param id      the {@link UUID} of the Channel
     * @return the {@link ChatChannel} which got the {@link UUID}
     */
    public static ChatChannel getById(final Session session, final UUID id) {
        return getById(session, ChatChannel.class, id);
    }

    /**
     * Returns a {@link List} of all existing {@link ChatChannel}s.
     *
     * @param session the sql {@link Session}
     * @return the {@link List} containg all existing {@link ChatChannel}s
     */
    public static List<ChatChannel> getChannels(final Session session) {
        return session.createQuery("select object (c) from ChatChannel c", ChatChannel.class)
                .getResultList();
    }

    /**
     * @param session the sql {@link Session} with transaction
     *                Deletes also the {@link ChatMessage}s and the {@link ChatChannelAccess}s from that channel.
     */
    @Override
    public void delete(final Session session) {
        session.createQuery("delete from ChatMessage as m where m.channel = :channel")
                .setParameter("channel", this)
                .executeUpdate();
        session.createQuery("delete from ChatChannelAccess as ca where ca.channel = :channel")
                .setParameter("channel", this)
                .executeUpdate();
        super.delete(session);
    }
}
