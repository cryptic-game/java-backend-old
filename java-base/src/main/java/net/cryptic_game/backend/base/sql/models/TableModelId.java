package net.cryptic_game.backend.base.sql.models;

import org.hibernate.Session;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public abstract class TableModelId extends TableModel {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    public UUID getId() {
        return this.id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    /**
     * Fetches the entity with the given id
     *
     * @param id The id of the entity
     * @return The instance of the fetched entity if it exists | null if the entity does not exist
     */
    public static <T extends TableModelId> T getById(Class<T> entityClass, final UUID id) {
        final Session session = sqlConnection.openSession();
        final T entity = session.find(entityClass, id);
        session.close();
        return entity;
    }
}
