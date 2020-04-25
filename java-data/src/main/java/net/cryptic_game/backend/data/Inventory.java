package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity representing an inventory entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "inventory")
public class Inventory extends TableModelAutoId {

    @Column(name = "element_name", updatable = true, nullable = true) // updatable?
    private String elementName;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    /**
     * Returns the element name of the {@link Inventory}
     *
     * @return Element name of the {@link Inventory}
     */
    public String getElementName() {
        return this.elementName;
    }

    /**
     * Sets a new element name for the {@link Inventory}
     *
     * @param elementName the new name to be set
     */
    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    /**
     * Returns the {@link User} who owns that {@link Inventory}
     *
     * @return {@link User} who owns the {@link Inventory}
     */
    public User getOwner() {
        return this.owner;
    }

    /**
     * Sets a new owner for the {@link Inventory}
     *
     * @param owner the new {@link User} to be set as owner
     */
    public void setOwner(final User owner) {
        this.owner = owner;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link Inventory} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("element_name", this.getElementName())
                .add("owner", this.getOwner().getId())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link Inventory}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link Inventory} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getElementName(), inventory.getElementName()) &&
                Objects.equals(getOwner(), inventory.getOwner()) &&
                Objects.equals(getId(), inventory.getId());
    }

    /**
     * Hashes the {@link Inventory} using {@link Objects} hash method
     *
     * @return Hash of the {@link Inventory}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId(), getElementName(), getOwner());
    }
}
