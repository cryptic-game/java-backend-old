package net.cryptic_game.backend.base.database.models;

import javax.persistence.*;

@MappedSuperclass
public abstract class TableModelAutoId extends TableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator")
    @SequenceGenerator(name = "generator", sequenceName = "sequence")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
