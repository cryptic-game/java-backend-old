package net.cryptic_game.backend.data.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "chat_channel")
public class Channel extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false)
    private String name;

    public static Channel createChannel(final String name) {
        final Channel channel = new Channel();
        channel.setName(name);

        channel.saveOrUpdate();
        return channel;
    }

    public static void removeChannel(final UUID id) {
        final Channel channel = getById(id);
        if (channel != null) {
            channel.delete();
        }
    }

    public static Channel getById(final UUID id) {
        return getById(Channel.class, id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(getName(), channel.getName()) &&
                Objects.equals(getId(), channel.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName());
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .build();
    }
}
