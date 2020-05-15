package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.currency.CurrencyWallet;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Entity representing a miner service entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "service_miner")
public class ServiceMiner extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "wallet", nullable = true, updatable = true)
    @Type(type = "uuid-char")
    private CurrencyWallet wallet;

    @Column(name = "started", updatable = false, nullable = true)
    private int started;

    @Column(name = "power", updatable = true, nullable = true)
    private float power;

    /**
     * Returns the {@link CurrencyWallet} where the {@link ServiceMiner} puts on money
     *
     * @return the {@link CurrencyWallet}
     */
    public CurrencyWallet getWallet() {
        return this.wallet;
    }

    /**
     * Sets a new {@link CurrencyWallet} for the {@link ServiceMiner}
     *
     * @param wallet the new {@link CurrencyWallet}
     */
    public void setWallet(final CurrencyWallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Returns the time when the {@link ServiceMiner} started
     *
     * @return the time started
     */
    public int getStarted() {
        return this.started;
    }

    /**
     * Sets a new time when the {@link ServiceMiner} started
     *
     * @param started the new time to be set
     */
    public void setStarted(final int started) {
        this.started = started;
    }

    /**
     * Returns the power of the {@link ServiceMiner}
     *
     * @return the power
     */
    public float getPower() {
        return this.power;
    }

    /**
     * Sets the power of the {@link ServiceMiner}
     *
     * @param power the new power
     */
    public void setPower(final float power) {
        this.power = power;
    }

    /**
     * Generates a {@link ServiceMiner} containing all relevant {@link ServiceMiner} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("wallet", this.getWallet().getId())
                .add("started", this.getStarted())
                .add("power", this.getPower())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link ServiceMiner}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link ServiceMiner} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceMiner that = (ServiceMiner) o;
        return getStarted() == that.getStarted() &&
                Float.compare(that.getPower(), getPower()) == 0 &&
                Objects.equals(getWallet(), that.getWallet());
    }

    /**
     * Hashes the {@link ServiceMiner} using {@link Objects} hash method
     *
     * @return Hash of the {@link ServiceMiner}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getWallet(), getStarted(), getPower());
    }
}
