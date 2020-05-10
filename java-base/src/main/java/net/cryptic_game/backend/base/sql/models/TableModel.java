package net.cryptic_game.backend.base.sql.models;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.SQLConnection;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class TableModel {

    protected static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    @JsonTransient
    @Version
    @Column(name = "version")
    private int version;

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public void saveOrUpdate() {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.saveOrUpdate(this);
        session.getTransaction().commit();
        session.close();
    }

    public void delete() {
        final Session session = sqlConnection.openSession();
        session.beginTransaction();
        session.delete(this);
        session.getTransaction().commit();
        session.close();
    }
}
