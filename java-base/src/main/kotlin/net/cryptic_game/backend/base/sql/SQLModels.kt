package net.cryptic_game.backend.base.sql

import net.cryptic_game.backend.base.interfaces.JsonSerializable
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*
import javax.persistence.*


@MappedSuperclass
abstract class TableModel : JsonSerializable {
    @Version
    @Column(name = "version")
    var version = 0

}

@MappedSuperclass
abstract class TableModelAutoId : TableModel() {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    var id: UUID? = null

}


@MappedSuperclass
abstract class TableModelId : TableModel() {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    var id: UUID? = null

}
