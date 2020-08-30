package net.cryptic_game.backend.data.entities.network;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.entities.device.Device;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a network entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_network")
@Data
public final class Network extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private Device owner;

    @Column(name = "public", updatable = true, nullable = false)
    private boolean isPublic;

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    /**
     * Creates a new {@link Network}.
     *
     * @param session  the sql {@link Session} with transaction
     * @param name     Name of the {@link Network}
     * @param owner    Owner of the {@link Network}
     * @param isPublic Public state of the {@link Network}
     * @return The instance of the created {@link Network}
     */
    public static Network createNetwork(final Session session, final String name, final Device owner, final boolean isPublic) {
        final Network network = new Network();
        network.setName(name);
        network.setOwner(owner);
        network.setPublic(isPublic);
        network.setCreated(OffsetDateTime.now());

        network.saveOrUpdate(session);
        return network;
    }

    /**
     * Fetches the {@link Network} with the given id.
     *
     * @param session the sql {@link Session}
     * @param id      The id of the {@link Network}
     * @return The instance of the fetched {@link Network} if it exists | null if the entity does not exist
     */
    public static Network getById(final Session session, final UUID id) {
        return getById(session, Network.class, id);
    }

    /**
     * Fetches the {@link Network} with the given name.
     *
     * @param session the sql session
     * @param name    The name of the {@link Network}
     * @return The instance of the fetched {@link Network} if it exists | null if the entity does not exist
     */
    public static Network getByName(final Session session, final String name) {
        return session.createQuery("select object (n) from Network as n where n.name = :name", Network.class)
                .setParameter("name", name)
                .getResultStream().findFirst().orElse(null);
    }

    /**
     * Fetches all {@link Network}'s owned by the give {@link Device}.
     *
     * @param session the sql {@link Session}
     * @param device  The {@link Device}
     * @return A {@link List} containing the fetched {@link Network}'s
     */
    public static List<Network> getNetworksOwnedByDevice(final Session session, final Device device) {
        return session.createQuery("select object (n) from Network as n where n.owner = :device", Network.class)
                .setParameter("device", device)
                .getResultList();
    }

    /**
     * Fetches all public {@link Network}'s.
     *
     * @param session the sql {@link Session}
     * @return A {@link List} containing the fetched {@link Network}'s
     */
    public static List<Network> getPublicNetworks(final Session session) {
        return session.createQuery("select object (n) from Network as n where n.isPublic = true", Network.class)
                .getResultList();
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Network} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("name", this.getName())
                .add("owner", this.getOwner().getId())
                .add("public", this.isPublic())
                .add("created", this.getCreated())
                .build();
    }
}
