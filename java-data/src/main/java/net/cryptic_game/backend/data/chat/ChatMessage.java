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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Entity representing a chat message entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "chat_message")
@Data
public class ChatMessage extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private ChatChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private ChatAction type;

    @Column(name = "text", updatable = false, nullable = true)
    private String text;

    /* User ID of the target, if it is not a whisper it is null and consequently targets everyone in the channel. */
    @OneToOne
    @JoinColumn(name = "target", updatable = false, nullable = true)
    private User target;

    /**
     * Sends a {@link ChatMessage} without a target and as {@link ChatAction#SEND_MESSAGE}.
     *
     * @param channel the {@link} Channel where the {@link ChatMessage} will be sent
     * @param user    the {@link User} who sends the {@link ChatMessage}
     * @param text    the content of the {@link ChatMessage}
     * @return the sent {@link ChatMessage}
     */
    public ChatMessage send(final ChatChannel channel, final User user, final String text) {
        return send(channel, user, ChatAction.SEND_MESSAGE, text);
    }

    /**
     * Sends a {@link ChatMessage} without a target.
     *
     * @param channel the {@link} Channel where the {@link ChatMessage} will be sent
     * @param user    the {@link User} who sends the {@link ChatMessage}
     * @param type    the {@link ChatAction}-Type of the {@link ChatMessage}
     * @param text    the content of the {@link ChatMessage}
     * @return the sent {@link ChatMessage}
     */
    public ChatMessage send(final ChatChannel channel, final User user, final ChatAction type, final String text) {
        return send(channel, user, null, type, text);
    }

    /**
     * Sends a {@link ChatMessage}.
     *
     * @param channel the {@link} Channel where the {@link ChatMessage} will be sent
     * @param user    the {@link User} who sends the {@link ChatMessage}
     * @param target  the {@link User} who receives the {@link ChatMessage}
     * @param type    the {@link ChatAction}-Type of the {@link ChatMessage}
     * @param text    the content of the {@link ChatMessage}
     * @return the sent {@link ChatMessage}
     */
    public ChatMessage send(final ChatChannel channel, final User user, final User target, final ChatAction type, final String text) {
        final ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setChannel(channel);
        message.setTarget(target);
        message.setType(type);
        message.setText(text);

        message.saveOrUpdate();
        return message;
    }

    /**
     * Returns a {@link List} of {@link ChatMessage}s sent in a {@link ChatChannel} by a {@link User}.
     *
     * @param user    the {@link User} who sent the {@link ChatMessage}s
     * @param channel the {@link ChatChannel} where the {@link ChatMessage}s were sent
     * @return the {@link List} of {@link ChatMessage}s
     */
    public List<ChatMessage> getMessages(final User user, final ChatChannel channel) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession
                    .createQuery("select object (m) from Message m where  m.channel = :channel "
                            + "and (m.target is null or (m.type = :whisper and m.target = :user) or (m.type = :whisper and m.user = :user))", ChatMessage.class)
                    .setParameter("user", user)
                    .setParameter("channel", channel)
                    .setParameter("whisper", ChatAction.WHISPER_MESSAGE)
                    .getResultList();
        }
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link ChatMessage} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("channel", this.getChannel().getId())
                .add("user", this.getUser().getId())
                .add("type", this.getType().toString())
                .add("target", this.getTarget().toString())
                .add("text", this.getText())
                .build();
    }
}
