package net.cryptic_game.backend.base.sql.models;

import lombok.Data;
import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.SQLConnection;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
@Data
public abstract class TableModel {

    protected static final SQLConnection SQL_CONNECTION;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        SQL_CONNECTION = app.getSqlConnection();
    }

    @JsonTransient
    @Version
    @Column(name = "version")
    private long version;

    public final void saveOrUpdate(final Session session) {
        session.saveOrUpdate(this);
    }

    /**
     * Deletes this entity.
     *
     * @param session the session
     */
    public void delete(final Session session) {
        session.delete(this);
    }
}
