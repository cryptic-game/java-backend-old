package net.cryptic_game.backend.data.currency;


import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Entity representing a currency transaction entry in the database
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_transaction")
public class CurrencyTransaction extends TableModelAutoId {

    @Column(name = "time_stamp", updatable = false, nullable = false)
    private LocalDateTime timeStamp;

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
     * Returns the {@link LocalDateTime}, the timestamp when the {@link CurrencyTransaction} has been made
     *
     * @return the timestamp
     */
    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Sets a new timestap as {@link LocalDateTime} for the {@link CurrencyTransaction}
     *
     * @param timeStamp the new {@link LocalDateTime} to be set
     */
    public void setTimeStamp(final LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Returns the {@link User} who sends the {@link CurrencyTransaction}
     *
     * @return the source {@link User}
     */
    public User getUserSource() {
        return this.userSource;
    }

    /**
     * Sets a new {@link User} as source for the {@link CurrencyTransaction}
     *
     * @param userSource the new {@link User} to be set
     */
    public void setUserSource(final User userSource) {
        this.userSource = userSource;
    }

    /**
     * Returns the {@link User} who receives the {@link CurrencyTransaction}
     *
     * @return the destination {@link User}
     */
    public User getUserDestination() {
        return this.userDestination;
    }

    /**
     * Sets a new {@link User} as destination for the {@link CurrencyTransaction}
     *
     * @param userDestination the new {@link User} to be set
     */
    public void setUserDestination(final User userDestination) {
        this.userDestination = userDestination;
    }

    /**
     * Returns the amount of transacted morphcoin
     *
     * @return the amount of money
     */
    public int getSendAmount() {
        return this.sendAmount;
    }

    /**
     * Sets a new amount of mophcoin to be transacted
     *
     * @param sendAmount the new amount of money to be set
     */
    public void setSendAmount(final int sendAmount) {
        this.sendAmount = sendAmount;
    }

    /**
     * Returns the purpose of the {@link CurrencyTransaction}
     *
     * @return the purpose
     */
    public String getPurpose() {
        return this.purpose;
    }

    /**
     * Sets a new purpose for the {@link CurrencyTransaction}
     *
     * @param purpose New purpose to be set.
     */
    public void setPurpose(final String purpose) {
        this.purpose = purpose;
    }

    /**
     * Returns the origin of the {@link CurrencyTransaction}
     *
     * @return the origin
     */
    public String getOrigin() {
        return this.origin;
    }

    /**
     * Sets a new origin of the {@link CurrencyTransaction}
     *
     * @param origin New origin to be set.
     */
    public void setOrigin(final String origin) {
        this.origin = origin;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyTransaction} information
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("time_stamp", this.getTimeStamp().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("source_id", this.getUserSource().getId())
                .add("SendAmount", this.getSendAmount())
                .add("destination_id", this.getUserDestination().getId())
                .add("usage", this.getPurpose())
                .add("origin", this.getOrigin())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link CurrencyTransaction}
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link CurrencyTransaction} | False if it does not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTransaction that = (CurrencyTransaction) o;
        return getSendAmount() == that.getSendAmount() &&
                Objects.equals(getTimeStamp(), that.getTimeStamp()) &&
                Objects.equals(getUserSource().getId(), that.getUserSource().getId()) &&
                Objects.equals(getUserDestination().getId(), that.getUserDestination().getId()) &&
                Objects.equals(getPurpose(), that.getPurpose()) &&
                Objects.equals(getOrigin(), that.getOrigin());
    }

    /**
     * Hashes the {@link CurrencyTransaction} using {@link Objects} hash method
     *
     * @return Hash of the {@link CurrencyTransaction}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getTimeStamp(), getUserSource().getId(), getSendAmount(), getUserDestination().getId(), getPurpose(), getOrigin());
    }
}
