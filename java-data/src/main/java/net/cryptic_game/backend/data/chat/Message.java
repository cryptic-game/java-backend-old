package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "chat_message")
public class Message extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(updatable = false)
    private ChatAction type;

    @Column(name = "text", updatable = false, nullable = true)
    private String text;

    /**
     * User ID of the target, if it is not a whisper it is null and consequently targets everyone in the channel.
     */
    @OneToOne
    @JoinColumn(name = "target", updatable = false, nullable = true)
    private User target;

    /**
     * Returns the {@link User} who send the {@link Message}
     *
     * @return the message's sender
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the {@link User} who sends the {@link Message}
     *
     * @param user the new {@link User}
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Returns the {@link Channel} where the {@link User} is sending the {@link Message}
     *
     * @return the {@link Channel}
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Sets a new {@link Channel} where the {@link Message} will be sent
     *
     * @param channel the new {@link Channel} to be set
     */
    public void setChannel(final Channel channel) {
        this.channel = channel;
    }

    /**
     * Returns the {@link ChatAction} which is triggered by the {@link Message}
     *
     * @return the {@link ChatAction}
     */
    public ChatAction getType() {
        return type;
    }

    /**
     * Sets a new {@link ChatAction} triggered by the {@link Message}
     *
     * @param type the new {@link ChatAction} to be set
     */
    public void setType(final ChatAction type) {
        this.type = type;
    }

    /**
     * Returns the Text of the {@link Message}
     *
     * @return the Text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets a new Text for the {@link Message}
     *
     * @param text the new Text to be set
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * Returns the {@link User} who receives the {@link Message}
     *
     * @return the targeted {@link User}
     */
    public User getTarget() {
        return target;
    }

    /**
     * Sets a new {@link User} as receiver for the {@link Message}
     *
     * @param target the new {@link User} to be set for receiving the {@link Message}
     */
    public void setTarget(final User target) {
        this.target = target;
    }

    /**
     * Sends a {@link Message} without a target and as "SEND_MESSAGE" {@link ChatAction}
     *
     * @param channel the {@link} Channel where the {@link Message} will be sent
     * @param user    the {@link User} who sends the {@link Message}
     * @param text    the content of the {@link Message}
     * @return the sent {@link Message}
     */
    public Message send(final Channel channel, final User user, final String text) {
        return send(channel, user, ChatAction.SEND_MESSAGE, text);
    }

    /**
     * Sends a {@link Message} without a target
     *
     * @param channel the {@link} Channel where the {@link Message} will be sent
     * @param user    the {@link User} who sends the {@link Message}
     * @param type    the {@link ChatAction}-Type of the {@link Message}
     * @param text    the content of the {@link Message}
     * @return the sent {@link Message}
     */
    public Message send(final Channel channel, final User user, final ChatAction type, final String text) {
        return send(channel, user, null, type, text);
    }

    /**
     * Sends a {@link Message}
     *
     * @param channel the {@link} Channel where the {@link Message} will be sent
     * @param user    the {@link User} who sends the {@link Message}
     * @param target  the {@link User} who receives the {@link Message}
     * @param type    the {@link ChatAction}-Type of the {@link Message}
     * @param text    the content of the {@link Message}
     * @return the sent {@link Message}
     */
    public Message send(final Channel channel, final User user, final User target, final ChatAction type, final String text) {
        final Message message = new Message();
        message.setUser(user);
        message.setChannel(channel);
        message.setTarget(target);
        message.setType(type);
        message.setText(text);

        message.saveOrUpdate();
        return message;
    }

    /**
     * Returns a {@link List} of {@link Message}s sent in a {@link Channel} by a {@link User}
     *
     * @param user    the {@link User} who sent the {@link Message}s
     * @param channel the {@link Channel} where the {@link Message}s were sent
     * @return the {@link List} of {@link Message}s
     */
    public List<Message> getMessages(final User user, final Channel channel) {
        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (m) from Message m where  m.channel = :channel " +
                            "and (m.target is null or (m.type = :whisper and m.target = :user) or (m.type = :whisper and m.user = :user))", Message.class)
                    .setParameter("user", user)
                    .setParameter("channel", channel)
                    .setParameter("whisper", ChatAction.WHISPER_MESSAGE)
                    .getResultList();
        }
    }

    /**
     * Compares an {@link Object} if it equals the {@link Message}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Message} | False if it does not
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        final Message message = (Message) o;
        return this.getId().equals(message.getId()) &&
                this.getUser().equals(message.getUser()) &&
                this.getChannel().equals(message.getChannel()) &&
                this.getType() == message.getType() &&
                this.getText().equals(message.getText()) &&
                Objects.equals(this.getTarget(), message.getTarget());
    }

    /**
     * Hashes the {@link Message} using {@link Objects} hash method
     *
     * @return Hash of the {@link Message}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getUser(), this.getChannel(), this.getType(), this.getText(), this.getTarget());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Message} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("channel", this.getChannel().getId())
                .add("user", this.getUser().getId())
                .add("type", this.getType().toString())
                .add("target", this.getTarget().toString())
                .add("text", this.getText())
                .build();
    }
}
