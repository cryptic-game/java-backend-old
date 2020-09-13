package net.cryptic_game.backend.data.sql.entities.currency;


import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Entity representing a currency transaction entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_transaction")
@Data
public final class CurrencyTransaction extends TableModelAutoId implements JsonSerializable {

    @Column(name = "timestamp", updatable = false, nullable = false)
    private OffsetDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "source_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private CurrencyWallet source;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private CurrencyWallet destination;

    @Column(name = "amount", updatable = false, nullable = false)
    private int amount;

    @Column(name = "purpose", updatable = false, nullable = true)
    private String purpose;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyTransaction} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("timestamp", this.getTimestamp())
                .add("source_id", this.getSource().getId())
                .add("destination_id", this.getDestination().getId())
                .add("amount", this.getAmount())
                .add("purpose", this.getPurpose())
                .build();
    }
}
