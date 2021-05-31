package net.cryptic_game.backend.admin.repository.server_management;

import net.cryptic_game.backend.admin.model.server_management.DisabledEndpointModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisabledEndpointRepository extends JpaRepository<DisabledEndpointModel, String> {

}
