package net.cryptic_game.backend.data.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.device.Device;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a network entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "network_network")
public class Network extends TableModelAutoId implements JsonSerializable {

    @Column(name = "name", updatable = true, nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "device_id", updatable = true, nullable = false)
    @Type(type = "uuid-char")
    private Device owner;

    @Column(name = "public", updatable = true, nullable = false)
    private boolean _public;

    @Column(name = "created", updatable = false, nullable = false)
    private ZonedDateTime created;

    /**
     * Empty constructor to create a new {@link Network}.
     */
    public Network() {
    }

    /**
     * Creates a new {@link Network}.
     *
     * @param name    Name of the {@link Network}
     * @param owner   Owner of the {@link Network}
     * @param _public Public state of the {@link Network}
     * @return The instance of the created {@link Network}
     */
    public static Network createNetwork(final String name, final Device owner, final boolean _public) {
        final ZonedDateTime now = ZonedDateTime.now();

        final Network network = new Network();
        network.setName(name);
        network.setOwner(owner);
        network.setPublic(_public);
        network.setCreated(now);

        final Session sqlSession = sqlConnection.openSession();
        sqlSession.beginTransaction();
        sqlSession.save(network);
        sqlSession.getTransaction().commit();
        sqlSession.close();
        return network;
    }

    /**
     * Fetches the {@link Network} with the given id.
     *
     * @param id The id of the {@link Network}
     * @return The instance of the fetched {@link Network} if it exists | null if the entity does not exist
     */
    public static Network getById(final UUID id) {
        return getById(Network.class, id);
    }

    /**
     * Fetches the {@link Network} with the given name.
     *
     * @param name The name of the {@link Network}
     * @return The instance of the fetched {@link Network} if it exists | null if the entity does not exist
     */
    public static Network getByName(final String name) {
        final Session sqlSession = sqlConnection.openSession();
        final List<Network> networks = sqlSession
                .createQuery("select object (n) from Network as n where n.name = :name", Network.class)
                .setParameter("name", name)
                .getResultList();
        sqlSession.close();

        if (!networks.isEmpty()) return networks.get(0);
        return null;
    }

    /**
     * Fetches all {@link Network}'s owned by the give {@link Device}.
     *
     * @param device The {@link Device}
     * @return A {@link List} containing the fetched {@link Network}'s
     */
    public static List<Network> getNetworksOwnedByDevice(final Device device) {
        final Session sqlSession = sqlConnection.openSession();
        final List<Network> networks = sqlSession
                .createQuery("select object (n) from Network as n where n.owner = :device", Network.class)
                .setParameter("device", device)
                .getResultList();
        sqlSession.close();
        return networks;
    }

    /**
     * Fetches all public {@link Network}'s.
     *
     * @return A {@link List} containing the fetched {@link Network}'s
     */
    public static List<Network> getPublicNetworks() {
        final Session sqlSession = sqlConnection.openSession();
        final List<Network> networks = sqlSession
                .createQuery("select object (n) from Network as n where n._public = true", Network.class)
                .getResultList();
        sqlSession.close();
        return networks;
    }

    /**
     * Returns the name of the {@link Network}.
     *
     * @return Name of the {@link Network}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets a new name of the {@link Network}.
     *
     * @param name New name to be set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the owner {@link Device} of the {@link Network}.
     *
     * @return Owner of the {@link Network}
     */
    public Device getOwner() {
        return this.owner;
    }

    /**
     * Sets a new owner {@link Device} of the {@link Network}.
     *
     * @param owner New owner to be set
     */
    public void setOwner(final Device owner) {
        this.owner = owner;
    }

    /**
     * Returns the public state of the {@link Network}.
     *
     * @return Public state of the {@link Network}
     */
    public boolean isPublic() {
        return this._public;
    }

    /**
     * Sets a new public state of the {@link Network}.
     *
     * @param _public New public state to be set
     */
    public void setPublic(final boolean _public) {
        this._public = _public;
    }

    /**
     * Returns the creation date of the {@link Network}.
     *
     * @return Creation date of the {@link Network}
     */
    public ZonedDateTime getCreated() {
        return this.created;
    }

    /**
     * Sets a new creation date of the {@link Network}.
     *
     * @param created New creation date to be set
     */
    public void setCreated(final ZonedDateTime created) {
        this.created = created;
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

    /**
     * Compares an {@link Object} if it equals the {@link Network}.
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Network} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return this.isPublic() == network.isPublic()
                && this.getName().equals(network.getName())
                && this.getOwner().equals(network.getOwner())
                && this.getCreated().equals(network.getCreated())
                && this.getId().equals(network.getId());
    }

    /**
     * Hashes the {@link Network} using {@link Objects} hash method.
     *
     * @return Hash of the {@link Network}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(), this.getOwner(), this.isPublic(), this.getCreated());
    }
}
