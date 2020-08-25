package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a chat message entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_message")
@Data
public final class ChatMessage extends TableModelAutoId implements JsonSerializable {

    public static final int MAX_MESSAGE_LENGTH = 1024;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private ChatChannel channel;

    @Column(name = "timestamp", updatable = false, nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "text", updatable = false, nullable = false, length = 1024)
    private String text;

    /* User ID of the target, if it is not a whisper it is null and consequently targets everyone in the channel. */
    @OneToOne
    @JoinColumn(name = "target", updatable = false, nullable = true)
    private User target;

    /**
     * Creates a new {@link ChatMessage} without a target (no whipser).
     *
     * @param session the sql session
     * @param channel the {@link ChatChannel} where the {@link ChatMessage} will be sent
     * @param user    the {@link User} who sends the {@link ChatMessage}
     * @param text    the content of the {@link ChatMessage}
     * @return the sent {@link ChatMessage}
     */
    public static ChatMessage create(final Session session, final ChatChannel channel, final User user, final String text) {
        return create(session, channel, user, null, text);
    }

    /**
     * Creates a new {@link ChatMessage}.
     *
     * @param session the sql session
     * @param channel the {@link ChatChannel} where the {@link ChatMessage} will be sent
     * @param user    the {@link User} who sends the {@link ChatMessage}
     * @param target  the {@link User} who receives the {@link ChatMessage}
     * @param text    the content of the {@link ChatMessage}
     * @return the sent {@link ChatMessage}
     */
    public static ChatMessage create(final Session session, final ChatChannel channel, final User user, final User target, final String text) {
        final ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setChannel(channel);
        message.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        message.setText(text);
        message.setTarget(target);

        message.saveOrUpdate(session);
        return message;
    }

    /**
     * Returns a {@link List} of {@link ChatMessage}s sent in a {@link ChatChannel}.
     *
     * @param session the sql session
     * @param channel the {@link ChatChannel} where the {@link ChatMessage}s were sent
     * @param user    the {@link User} of the request
     * @param offset  the offset of the {@link ChatMessage}s
     * @param count   the count of the {@link ChatMessage}s
     * @return the {@link List} of {@link ChatMessage}s
     */
    public static List<ChatMessage> getMessages(final Session session, final ChatChannel channel, final User user, final int offset, final int count) {
        return session.createQuery("select object (m) from ChatMessage m where m.channel = :channel "
                + "and (m.target is null or m.target = :user or m.user = :user) "
                + "order by m.timestamp desc", ChatMessage.class)
                .setParameter("channel", channel)
                .setParameter("user", user)
                .setFirstResult(offset)
                .setMaxResults(count)
                .getResultList();
    }

    /**
     * Returns a {@link List} of all {@link ChatMessage}s from on {@link ChatChannel}.
     *
     * @param session the sql session
     * @param channel the {@link ChatChannel} whose {@link ChatMessage}s will be returned
     * @return the {@link List} of {@link ChatMessage}s
     */
    public static List<ChatMessage> getMessages(final Session session, final ChatChannel channel) {
        return session.createQuery("select object (m) from ChatMessage m where m.channel = :channel ", ChatMessage.class)
                .setParameter("channel", channel)
                .getResultList();
    }

    public static ChatMessage getById(final Session session, final UUID id) {
        return getById(session, ChatMessage.class, id);
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatMessage} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("channel_id", this.getChannel().getId())
                .add("user_id", this.getUser().getId())
                .add("target_id", this.target == null ? null : this.getTarget().getId())
                .add("text", this.getText())
                .add("timestamp", this.timestamp)
                .build();
    }
}
