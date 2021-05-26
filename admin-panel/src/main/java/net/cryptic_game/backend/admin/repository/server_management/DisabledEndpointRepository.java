package net.cryptic_game.backend.admin.repository.server_management;

import net.cryptic_game.backend.admin.model.server_management.DisabledEndpointModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisabledEndpointRepository extends JpaRepository<DisabledEndpointModel, String> {

    List<DisabledEndpointModel> findAllByReason(String reason);
}
