package net.cryptic_game.backend.base.sql.models;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class TableModelId extends TableModel {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
