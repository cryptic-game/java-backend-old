package net.cryptic_game.backend.base.database.models;

import net.cryptic_game.backend.base.interfaces.JsonSerializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public abstract class TableModel implements JsonSerializable {

    @Version
    @Column(name = "version")
    private int version;

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }
}
