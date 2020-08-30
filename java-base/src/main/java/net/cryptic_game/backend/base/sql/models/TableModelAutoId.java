package net.cryptic_game.backend.base.sql.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Getter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class TableModelAutoId extends TableModel {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
}
