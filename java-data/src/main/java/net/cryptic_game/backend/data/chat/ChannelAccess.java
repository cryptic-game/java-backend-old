package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.client.ApiClient;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "chat_channel_access")
public class ChannelAccess extends TableModelAutoId {

    private static final Logger log = LoggerFactory.getLogger(ChannelAccess.class);

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private Channel channel;

    public static ChannelAccess join(final User user, final Channel channel, final Set<ApiClient> notifyUsers) {
        ChannelAccess channelAccess = new ChannelAccess();
        channelAccess.setUser(user);
        channelAccess.setChannel(channel);

        channelAccess.saveOrUpdate();
        return channelAccess;
    }

    public static boolean leave(final User user, final Channel channel, Set<ApiClient> notifyUsers) {
        try (Session sqlSession = sqlConnection.openSession()) {
            sqlSession
                    .createQuery("delete from ChannelAccess ca where ca.channel = :channel and ca.user = :user")
                    .setParameter("channel", channel)
                    .setParameter("user", user);
            return true;
        } catch (HibernateException e) {
            log.error("The user wasn't found in the channel. Therefore he can't leave the channel", e);
            return false;
        }
    }

    public static List<User> getMembers(final Channel channel) {

        try (Session sqlSession = sqlConnection.openSession()) {
            return sqlSession
                    .createQuery("select object (u) from User u where u.id in (select ca.user from ChannelAccess ca where ca.channel = :channel)", User.class)
                    .setParameter("channel", channel)
                    .getResultList();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(final Channel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ChannelAccess)) return false;
        final ChannelAccess that = (ChannelAccess) o;
        return this.getId().equals(that.getId()) &&
                this.getUser().equals(that.getUser()) &&
                this.getChannel().equals(that.getChannel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getUser(), this.getChannel());
    }

    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("user_id", this.getUser().getId())
                .add("channel_id", this.getChannel().getId())
                .build();
    }
}

