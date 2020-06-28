package net.cryptic_game.backend.data.currency;


import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * Entity representing a currency transaction entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_transaction")
@Data
public class CurrencyTransaction extends TableModelAutoId implements JsonSerializable {

    @Column(name = "timestamp", updatable = false, nullable = false)
    private ZonedDateTime timeStamp;

    @ManyToOne
    @JoinColumn(name = "source_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User userSource;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User userDestination;

    @Column(name = "send_amount", updatable = false, nullable = false)
    private int sendAmount;

    @Column(name = "purpose", updatable = false, nullable = true)
    private String purpose;

    @Column(name = "origin", updatable = false, nullable = true)
    private String origin;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyTransaction} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("timestamp", this.getTimeStamp())
                .add("source_id", this.getUserSource().getId())
                .add("SendAmount", this.getSendAmount())
                .add("destination_id", this.getUserDestination().getId())
                .add("usage", this.getPurpose())
                .add("origin", this.getOrigin())
                .build();
    }
}
