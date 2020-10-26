package net.cryptic_game.backend.data.sql.repositories.server_management;

import net.cryptic_game.backend.data.sql.entities.server_management.DisabledEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisabledEndpointRepository extends JpaRepository<DisabledEndpoint, String> {

    default DisabledEndpoint create(String path) {
        return this.save(new DisabledEndpoint(path));
    }
}
