package net.cryptic_game.backend.data.sql.repositories.network;

import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.network.NetworkInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NetworkInvitationRepository extends JpaRepository<NetworkInvitation, UUID> {

    List<NetworkInvitation> findAllByKeyDevice(Device device);

    List<NetworkInvitation> findAllByKeyNetwork(Network network);

    Optional<NetworkInvitation> findByKeyNetworkAndKeyDevice(Network network, Device device);
}
