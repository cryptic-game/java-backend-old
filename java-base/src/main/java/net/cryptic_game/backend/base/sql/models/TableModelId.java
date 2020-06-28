package net.cryptic_game.backend.base.sql.models;

import lombok.Data;
import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
@Data
public abstract class TableModelId extends TableModel {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    /**
     * Fetches the entity with the given id.
     *
     * @param id          The id of the entity
     * @param entityClass Model class of the entity
     * @param <T>         Model type of the entity
     * @return The instance of the fetched entity if it exists | null if the entity does not exist
     */
    public static <T extends TableModelId> T getById(final Class<T> entityClass, final UUID id) {
        final Session session = SQL_CONNECTION.openSession();
        final T entity = session.find(entityClass, id);
        session.close();
        return entity;
    }
}
