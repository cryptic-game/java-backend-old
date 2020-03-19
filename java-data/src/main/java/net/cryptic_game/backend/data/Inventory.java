package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inventory")
public class Inventory extends TableModelAutoId {

    @Column(name = "element_name", updatable = true, nullable = true) // updatable?
    private String elementName;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User owner;

    public String getElementName() {
        return this.elementName;
    }

    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }

    public User getOwner() {
        return this.owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("element_name", this.getElementName())
                .add("owner", this.getOwner().getId())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getElementName(), inventory.getElementName()) &&
                Objects.equals(getOwner(), inventory.getOwner()) &&
                Objects.equals(getId(), inventory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getElementName(), getOwner());
    }
}
