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
@ApiEndpointCollection(id = "network/member", type = ApiType.REST)
public final class NetworkMemberEndpoints {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;
    private final NetworkRepository networkRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final NetworkInvitationRepository networkInvitationRepository;

    @ApiEndpoint(id = "members")
    public ApiResponse member(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
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

        return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.findAllByKeyDevice(device));
    }

    @ApiEndpoint(id = "invitations")
    public ApiResponse invitations(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
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

        return new ApiResponse(HttpResponseStatus.OK, this.networkInvitationRepository.findAllByKeyDevice(device));
    }

    @ApiEndpoint(id = "leave")
    public ApiResponse leave(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                             @ApiParameter(id = "device_id") final UUID deviceId,
                             @ApiParameter(id = "network_id") final UUID id) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);
        final Network network = this.networkRepository.findById(id).orElse(null);

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        if (!this.deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NETWORK");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "CAN_NOT_LEAVE_OWN_NETWORK");
        }

        final NetworkMember member = this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).orElse(null);
        if (member == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MEMBER");
        }
        this.networkMemberRepository.delete(member);
        return new ApiResponse(HttpResponseStatus.OK);
    }
}
