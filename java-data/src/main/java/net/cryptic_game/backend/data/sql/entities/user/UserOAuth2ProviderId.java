package net.cryptic_game.backend.data.sql.entities.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.cryptic_game.backend.base.jpa.model.TableModel;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Cacheable
@Table(name = "user_oauth2_provider_id")
public final class UserOAuth2ProviderId extends TableModel {

    @EmbeddedId
    private ProviderUserId providerUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ProviderUserId implements Serializable {

        @Column(name = "id", updatable = false, nullable = false)
        private String id;

        @Column(name = "provider", updatable = false, nullable = false)
        private String provider;

        @Override
        public boolean equals(final Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof ProviderUserId)) return false;
            ProviderUserId element = (ProviderUserId) obj;
            return this.id.equals(element.id) && this.provider.equals(element.provider);
        }

        /**
         * @return the hashcode
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, provider);
        }
    }
}
