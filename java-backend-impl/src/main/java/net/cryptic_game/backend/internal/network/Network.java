package net.cryptic_game.backend.internal.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.internal.device.DeviceImpl;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "network_network")
public class Network extends TableModelAutoId {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private DeviceImpl owner;

    @Column(name = "hidden", updatable = true, nullable = false)
    private boolean hidden;

    @Column(name = "created", updatable = false, nullable = false)
    private LocalDateTime created;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public DeviceImpl getOwner() {
        return this.owner;
    }

    public void setOwner(final DeviceImpl owner) {
        this.owner = owner;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(final boolean hidden) {
        this.hidden = hidden;
    }

    public LocalDateTime getCreated() {
        return this.created;
    }

    public void setCreated(final LocalDateTime created) {
        this.created = created;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("name", this.getName())
                .add("owner", this.getOwner().getId())
                .add("hidden", this.isHidden())
                .add("created", this.getCreated().toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return this.isHidden() == network.isHidden() &&
                this.getName().equals(network.getName()) &&
                this.getOwner().equals(network.getOwner()) &&
                this.getCreated().equals(network.getCreated()) &&
                this.getId().equals(network.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getOwner(), this.isHidden(), this.getCreated());
    }
}
