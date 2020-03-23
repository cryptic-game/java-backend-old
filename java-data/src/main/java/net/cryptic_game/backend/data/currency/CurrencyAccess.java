package net.cryptic_game.backend.data.currency;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.base.utils.JsonBuilder;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "currency_access")
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
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public CurrencyWallet getWallet() {
        return this.wallet;
    }

    public void setWallet(final CurrencyWallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("id", this.getId())
                .add("user_id", this.getUser().getId())
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
