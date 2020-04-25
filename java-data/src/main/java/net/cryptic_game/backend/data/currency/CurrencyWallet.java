package net.cryptic_game.backend.data.currency;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Entity representing a currency wallet entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_wallet")
public class CurrencyWallet extends TableModelAutoId {

    @Column(name = "time_stamp", updatable = false, nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "password", updatable = true, nullable = true)
    private String password;

    @Column(name = "amount", updatable = true, nullable = false)
    private int amount;

    /**
     * Returns the {@link LocalDateTime} of the {@link CurrencyWallet}
     *
     * @return the {@link LocalDateTime}
     */
    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Sets a new {@link LocalDateTime} as timestamp for the {@link CurrencyWallet}
     *
     * @param timeStamp the new {@link LocalDateTime} to be set
     */
    public void setTimeStamp(final LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Returns the password of the {@link CurrencyWallet}
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets a new password for the {@link CurrencyWallet}
     *
     * @param sendAmount the new password to be set
     */
    public void setPassword(final String sendAmount) {
        this.password = sendAmount;
    }

    /**
     * Returns the Amount of Morphcoins held in the {@link CurrencyWallet}
     *
     * @return the amount of Morphcoins
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Sets a new amount of morphcoins help in the {@link CurrencyWallet}
     *
     * @param amount the new amount to be set
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }

    /**
     * Compares an {@link Object} if it equals the {@link CurrencyWallet}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link CurrencyWallet} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyWallet that = (CurrencyWallet) o;
        return getAmount() == that.getAmount() &&
                Objects.equals(getTimeStamp(), that.getTimeStamp()) &&
                Objects.equals(getPassword(), that.getPassword());
    }

    /**
     * Hashes the {@link CurrencyWallet} using {@link Objects} hash method
     *
     * @return Hash of the {@link CurrencyWallet}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTimeStamp(), getPassword(), getAmount());
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyWallet} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("time_stamp", this.getTimeStamp().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("send_amount", this.getPassword())
                .add("destination_uuid", this.getAmount())
                .build();
    }
}
