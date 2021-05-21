package net.cryptic_game.backend.endpoints.network;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.network.NetworkMember;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkInvitationRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkMemberRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "network/owner", type = ApiType.REST)
public final class NetworkOwnerEndpoints {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;
    private final NetworkRepository networkRepository;
    private final NetworkInvitationRepository networkInvitationRepository;
    private final NetworkMemberRepository networkMemberRepository;

    @ApiEndpoint(id = "list")
    public ApiResponse list(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        if (!this.deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(HttpResponseStatus.OK, this.networkRepository.findAllByOwner(device));
    }

    @ApiEndpoint(id = "invitations")
    public ApiResponse invitations(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                                   @ApiParameter(id = "network_id") final UUID networkId) {
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final User user = this.userRepository.findById(userId).orElseThrow();

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (!this.deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(HttpResponseStatus.OK, this.networkInvitationRepository.findAllByKeyNetwork(network));
    }

    @ApiEndpoint(id = "kick")
    public ApiResponse kick(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "network_id") final UUID networkId,
                            @ApiParameter(id = "device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (!this.deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "CAN_NOT_KICK_OWNER");
        }

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        final NetworkMember networkMember = this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).orElse(null);

        if (networkMember == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MEMBER");
        }

        this.networkMemberRepository.delete(networkMember);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "delete")
    public ApiResponse delete(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "network_id") final UUID networkId) {
        User user = this.userRepository.findById(userId).orElse(null);
        Network network = this.networkRepository.findById(networkId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (!this.deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        this.networkMemberRepository.deleteAllByKeyNetwork(network);
        this.networkInvitationRepository.deleteAllByKeyNetwork(network);
        this.networkRepository.delete(network);

        return new ApiResponse(HttpResponseStatus.OK);
    }
}
