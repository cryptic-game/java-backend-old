package net.cryptic_game.backend.data.currency;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * Entity representing a currency wallet entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_wallet")
@Data
public class CurrencyWallet extends TableModelAutoId implements JsonSerializable {

    @Column(name = "timestamp", updatable = false, nullable = false)
    private ZonedDateTime timeStamp;

    @Column(name = "password", updatable = true, nullable = true)
    private String password;

    @Column(name = "amount", updatable = true, nullable = false)
    private int amount;

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyWallet} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("timestamp", this.getTimeStamp())
                .add("send_amount", this.getPassword())
                .add("destination_uuid", this.getAmount())
                .build();
    }
}
