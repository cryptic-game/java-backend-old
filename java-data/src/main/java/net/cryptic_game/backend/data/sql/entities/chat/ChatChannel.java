package net.cryptic_game.backend.data.sql.entities.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.getnova.framework.jpa.model.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity representing a chat channel entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_channel")
public final class ChatChannel extends TableModelAutoId {

    public static final int MAX_NAME_LENGTH = 32;

    @Column(name = "name", updatable = true, nullable = false, length = MAX_NAME_LENGTH)
    private String name;
}
