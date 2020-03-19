package net.cryptic_game.backend.data;


import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "currency_transaction")
public class CurrencyTransaction extends TableModelAutoId {

    @Column(name = "time_stamp", updatable = false, nullable = false)
    private LocalDateTime time_stamp;

    @ManyToOne
    @JoinColumn(name = "source_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user_source;

    @ManyToOne
    @JoinColumn(name = "destination_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private User user_destination;

    @Column(name = "send_amount", updatable = false, nullable = false)
    private int sendAmount;

    @Column(name = "usage", updatable = false, nullable = true)
    private String usage;

    @Column(name = "origin", updatable = false, nullable = true)
    private String origin;

    public LocalDateTime getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(LocalDateTime time_stamp) {
        this.time_stamp = time_stamp;
    }

    public User getUser_source() {
        return user_source;
    }

    public void setUser_source(User user_source) {
        this.user_source = user_source;
    }

    public User getUser_destination() {
        return user_destination;
    }

    public void setUser_destination(User user_destination) {
        this.user_destination = user_destination;
    }

    public int getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(int sendAmount) {
        this.sendAmount = sendAmount;
    }


    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("time_stamp", this.getTime_stamp().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("source_id", this.getUser_source().getId())
                .add("SendAmount", this.getSendAmount())
                .add("destination_id", this.getUser_destination().getId())
                .add("usage", this.getUsage())
                .add("origin", this.getOrigin())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyTransaction that = (CurrencyTransaction) o;
        return getSendAmount() == that.getSendAmount() &&
                Objects.equals(getTime_stamp(), that.getTime_stamp()) &&
                Objects.equals(getUser_source().getId(), that.getUser_source().getId()) &&
                Objects.equals(getUser_destination().getId(), that.getUser_destination().getId()) &&
                Objects.equals(getUsage(), that.getUsage()) &&
                Objects.equals(getOrigin(), that.getOrigin());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTime_stamp(), getUser_source().getId(), getSendAmount(), getUser_destination().getId(), getUsage(), getOrigin());
    }


}
