package net.cryptic_game.backend.data.currency;

import com.google.gson.JsonObject;
import lombok.Data;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity representing a currency access entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Table(name = "currency_access")
@Data
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
}
