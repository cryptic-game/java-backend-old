package net.cryptic_game.backend.data.service;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.currency.CurrencyWallet;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "service_miner")
public class ServiceMiner extends TableModelAutoId {

    @ManyToOne
    @JoinColumn(name = "wallet", nullable = true, updatable = true)
    @Type(type = "uuid-char")
    private CurrencyWallet wallet;

    @Column(name = "started", updatable = false, nullable = true)
    private int started;

    @Column(name = "power", updatable = true, nullable = true)
    private float power;

    public CurrencyWallet getWallet() {
        return this.wallet;
    }

    public void setWallet(final CurrencyWallet wallet) {
        this.wallet = wallet;
    }

    public int getStarted() {
        return this.started;
    }

    public void setStarted(final int started) {
        this.started = started;
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(final float power) {
        this.power = power;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("wallet", this.getWallet().getId())
                .add("started", this.getStarted())
                .add("power", this.getPower())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceMiner that = (ServiceMiner) o;
        return getStarted() == that.getStarted() &&
                Float.compare(that.getPower(), getPower()) == 0 &&
                Objects.equals(getWallet(), that.getWallet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWallet(), getStarted(), getPower());
    }
}
