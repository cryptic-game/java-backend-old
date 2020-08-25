package net.cryptic_game.backend.base.sql.models;

import lombok.Data;
import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class TableModelAutoId extends TableModel {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    /**
     * Fetches the entity with the given id.
     *
     * @param session     The sql session
     * @param id          The id of the entity
     * @param entityClass Model class of the entity
     * @param <T>         Model type of the entity
     * @return The instance of the fetched entity if it exists | null if the entity does not exist
     */
    public static <T extends TableModelAutoId> T getById(final Session session, final Class<T> entityClass, final UUID id) {
        return session.find(entityClass, id);
    }
}
