package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

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
     * Joins a {@link User} to a {@link ChatChannel} and returns the {@link ChatChannelAccess}.
     *
     * @param user    the {@link User} who join
     * @param channel the {@link ChatChannel} where to join
     * @return the resulting {@link ChatChannelAccess}
     */
    public static ChatChannelAccess create(final User user, final ChatChannel channel) {
        ChatChannelAccess channelAccess = new ChatChannelAccess();
        channelAccess.setUser(user);
        channelAccess.setChannel(channel);

        channelAccess.saveOrUpdate();
        return channelAccess;
    }

    /**
     * Get the {@link ChatChannelAccess} of a {@link User} to a {@link ChatChannel}.
     *
     * @param user    the {@link User}
     * @param channel the {@link ChatChannel}
     * @return the {@link ChatChannelAccess} if found | otherwise {@code null}
     */
    public static ChatChannelAccess get(final User user, final ChatChannel channel) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession.createQuery("select object (ca) from ChatChannelAccess ca "
                    + "where ca.channel = :channel and ca.user = :user", ChatChannelAccess.class)
                    .setParameter("channel", channel)
                    .setParameter("user", user)
                    .setMaxResults(1)
                    .getResultStream()
                    .findFirst().orElse(null);
        }
    }

    /**
     * Returns a {@link List} of {@link ChatChannel}s, which contains the {@link User}.
     *
     * @param user the {@link User}, whose {@link ChatChannel}s will be returned
     * @return the {@link List} of {@link ChatChannel}s
     */
    public static List<ChatChannel> getChannels(final User user) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession.createQuery("select object (c) from ChatChannel c where c in "
                    + "(select ca.channel from ChatChannelAccess ca where ca.user = :user)", ChatChannel.class)
                    .setParameter("user", user)
                    .getResultList();
        }
    }

    /**
     * Returns a {@link List} of {@link User}s, who are in a {@link ChatChannel}.
     *
     * @param channel the {@link ChatChannel} where to get the {@link User}s from
     * @return the {@link List} of {@link User}s in the {@link ChatChannel}
     */
    public static List<User> getMembers(final ChatChannel channel) {
        try (Session sqlSession = SQL_CONNECTION.openSession()) {
            return sqlSession.createQuery("select object (u) from User u where u in "
                    + "(select ca.user from ChatChannelAccess ca where ca.channel = :channel)", User.class)
                    .setParameter("channel", channel)
                    .getResultList();
        }
    }

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

