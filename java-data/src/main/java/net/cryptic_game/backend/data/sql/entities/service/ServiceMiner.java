package net.cryptic_game.backend.data.sql.entities.service;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.sql.entities.currency.CurrencyWallet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a miner service entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_miner")
public final class ServiceMiner extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = true)
    private CurrencyWallet wallet;

    @Column(name = "started", updatable = true, nullable = false)
    private int started;

    @Column(name = "power", updatable = true, nullable = false)
    private float power;

    /**
     * Generates a {@link ServiceMiner} containing all relevant {@link ServiceMiner} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("wallet_id", this.getWallet().getId())
                .add("started", this.getStarted())
                .add("power", this.getPower())
                .build();
    }
}
