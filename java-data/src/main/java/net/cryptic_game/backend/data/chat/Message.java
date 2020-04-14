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
     * User ID of the target, if it is not a whisper it is null and consequently targets everyone in the channel
     */
    @OneToOne
    @Column(name = "target", updatable = false, nullable = true)
    private User target;

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(final Channel channel) {
        this.channel = channel;
    }

    public ChatAction getType() {
        return type;
    }

    public void setType(final ChatAction type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(final User target) {
        this.target = target;
    }

    public Message send(final Channel channel, final User user, final String text) {
        return send(channel, user, ChatAction.SEND_MESSAGE, text);
    }

    public Message send(final Channel channel, final User user, final ChatAction type, final String text) {
        return send(channel, user, null, type, text);
    }

    public Message send(final Channel channel, final User user, final User target, final ChatAction type, final String text) {
        Session sqlSession = sqlConnection.openSession();

        Message message = new Message();
        message.setUser(user);
        message.setChannel(channel);
        message.setTarget(target);
        message.setType(type);
        message.setText(text);

        message.saveOrUpdate();
        return message;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(getUser(), message.getUser()) &&
                Objects.equals(getChannel(), message.getChannel()) &&
                getType() == message.getType() &&
                Objects.equals(getText(), message.getText()) &&
                Objects.equals(getTarget(), message.getTarget()) &&
                Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUser(), getChannel(), getType(), getText(), getTarget());
    }

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
