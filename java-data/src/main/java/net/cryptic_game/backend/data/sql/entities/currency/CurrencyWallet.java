package net.cryptic_game.backend.data.sql.entities.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.json.JsonTransient;
import net.cryptic_game.backend.base.sql.models.TableModelAutoId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entity representing a currency wallet entry in the database.
 *
 * @since 0.3.0
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "currency_wallet")
public final class CurrencyWallet extends TableModelAutoId {

    @Column(name = "created", updatable = false, nullable = false)
    private OffsetDateTime created;

    @JsonTransient
    @Column(name = "password", updatable = true, nullable = false)
    private String passwordHash;

    @Column(name = "balance", updatable = true, nullable = false)
    private BigDecimal balance;
}
