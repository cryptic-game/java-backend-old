package net.cryptic_game.backend.data;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.ZoneOffset;
import java.util.Objects;

public class CurrencyAccess extends TableModelAutoId {



    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private CurrencyWallet wallet;

    public User getUser() {
        return user;
    }

    public void setUser(User user_source) {
        this.user = user_source;
    }

    public CurrencyWallet getWallet() {
        return wallet;
    }

    public void setWallet(CurrencyWallet wallet) {
        this.wallet = wallet;
    }


    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add( "user_id", this.getUser().getId())
                .add("wallet_id", this.getWallet().getId())
                .build();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyAccess that = (CurrencyAccess) o;
        return Objects.equals(getUser(), that.getUser()) &&
                Objects.equals(getWallet(), that.getWallet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getWallet());
    }
}
