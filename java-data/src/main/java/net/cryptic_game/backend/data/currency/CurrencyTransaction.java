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

    public LocalDateTime getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(final LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getUserSource() {
        return this.userSource;
    }

    public void setUserSource(final User userSource) {
        this.userSource = userSource;
    }

    public User getUserDestination() {
        return this.userDestination;
    }

    public void setUserDestination(final User userDestination) {
        this.userDestination = userDestination;
    }

    public int getSendAmount() {
        return this.sendAmount;
    }

    public void setSendAmount(final int sendAmount) {
        this.sendAmount = sendAmount;
    }

    public String getPurpose() {
        return this.purpose;
    }

    public void setPurpose(final String purpose) {
        this.purpose = purpose;
    }

    public String getOrigin() {
        return this.origin;
    }

    public void setOrigin(final String origin) {
        this.origin = origin;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(getTimeStamp(), getUserSource().getId(), getSendAmount(), getUserDestination().getId(), getPurpose(), getOrigin());
    }


}
