package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Entity
@Table(name = "currency_wallet")
public class CurrencyWallet extends TableModelAutoId {

    @Column(name = "time_stamp", updatable = false, nullable = false)
    private LocalDateTime timeStamp;


    @Column(name = "key", updatable = true, nullable = true)
    private String key;

    @Column(name = "amount", updatable = true, nullable = false)
    private int amount;


    public LocalDateTime getTimeStamp() { return timeStamp; }

    public void setTime_stamp(LocalDateTime time_stamp) { this.timeStamp = timeStamp; }


    public String getKey() { return key; }

    public void setKey(String send_amount) { this.key = send_amount; }

    public int getAmount() { return amount; }

    public void setAmount(int amount) { this.amount = amount; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyWallet that = (CurrencyWallet) o;
        return getAmount() == that.getAmount() &&
                Objects.equals(getTimeStamp(), that.getTimeStamp()) &&
                Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTimeStamp(), getKey(), getAmount());
    }


    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add( "time_stamp", this.getTimeStamp().toInstant(ZoneOffset.UTC).toEpochMilli())
                .add("send_amount", this.getKey())
                .add("destination_uuid", this.getAmount())
                .build();
    }
}
