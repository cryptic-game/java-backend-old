package net.cryptic_game.backend.data.currency;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Entity representing a currency access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_access")
public class CurrencyAccess extends TableModelAutoId implements JsonSerializable {

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private User user;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false, updatable = false)
    @Type(type = "uuid-char")
    private CurrencyWallet wallet;

    /**
     * Returns the {@link User} of the {@link CurrencyAccess}.
     *
     * @return the {@link User}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Sets a new {@link User} for the {@link CurrencyAccess}.
     *
     * @param user the new {@link User} to be set
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * Returns the {@link CurrencyWallet} of the {@link CurrencyAccess}.
     *
     * @return the {@link CurrencyAccess}
     */
    public CurrencyWallet getWallet() {
        return this.wallet;
    }

    /**
     * Sets a new {@link CurrencyAccess}.
     *
     * @param wallet the new {@link CurrencyAccess} to be set
     */
    public void setWallet(final CurrencyWallet wallet) {
        this.wallet = wallet;
    }

    /**
     * Generates a {@link JsonObject} containing all relevant {@link CurrencyTransaction} information.
     *
     * @return The generated {@link JsonObject}
     */
    @Override
    public JsonObject serialize() {
        return JsonBuilder.create("id", this.getId())
                .add("user_id", this.getUser().getId())
                .add("wallet_id", this.getWallet().getId())
                .build();
    }

    /**
     * Compares an {@link Object} if it equals the {@link CurrencyAccess}.
     *
     * @param o {@link Object} to compare
     * @return True if the {@link Object} equals the {@link CurrencyAccess} | False if it does not
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrencyAccess that = (CurrencyAccess) o;
        return Objects.equals(getUser(), that.getUser())
                && Objects.equals(getWallet(), that.getWallet());
    }

    /**
     * Hashes the {@link CurrencyAccess} using {@link Objects} hash method.
     *
     * @return Hash of the {@link CurrencyAccess}
     */
    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getWallet());
    }
}
