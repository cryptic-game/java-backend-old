package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inventory")
public class Inventory extends TableModelAutoId {


    @Column(name = "element_name", updatable = true, nullable = true) // updatable?
    private String element_name;

    @Column(name = "related_ms", updatable = true, nullable = true)
    private String related_ms;

    @ManyToOne
    @JoinColumn(name = "owner", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User owner;


    public String getElement_name() {
        return element_name;
    }

    public void setElement_name(String element_name) {
        this.element_name = element_name;
    }

    public String getRelated_ms() {
        return related_ms;
    }

    public void setRelated_ms(String related_ms) {
        this.related_ms = related_ms;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("element_name", this.getElement_name())
                .add("related_ms", this.getRelated_ms())
                .add("owner", this.getOwner().getId())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(getElement_name(), inventory.getElement_name()) &&
                Objects.equals(getRelated_ms(), inventory.getRelated_ms()) &&
                Objects.equals(getOwner(), inventory.getOwner());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getElement_name(), getRelated_ms(), getOwner());
    }
}
