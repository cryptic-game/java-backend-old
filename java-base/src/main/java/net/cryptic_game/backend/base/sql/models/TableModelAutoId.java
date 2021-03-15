package net.cryptic_game.backend.base.sql.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class TableModelAutoId extends TableModel {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Checks if the objects are from the same class and if the uuid is the same.
     *
     * @param obj the object to check
     * @return true if equal else false
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (!(this.getClass().isInstance(obj))) return false;
        TableModelAutoId element = (TableModelAutoId) obj;
        return this.id.equals(element.getId());
    }

    /**
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
