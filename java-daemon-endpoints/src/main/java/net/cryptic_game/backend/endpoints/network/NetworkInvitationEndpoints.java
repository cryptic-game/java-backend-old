package net.cryptic_game.backend.endpoints.network;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.DaemonAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.network.NetworkInvitation;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkInvitationRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkMemberRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@ApiEndpointCollection(id = "network/invitation", type = ApiType.REST, authenticator = DaemonAuthenticator.class)
public final class NetworkInvitationEndpoints {

    private final UserRepository userRepository;
    private final NetworkRepository networkRepository;
    private final NetworkInvitationRepository networkInvitationRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;

    @ApiEndpoint(id = "accept")
    public ApiResponse accept(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "network_id") final UUID networkId,
                              @ApiParameter(id = "device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
    }


    @ApiEndpoint(id = "deny")
    public ApiResponse deny(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "network_id") final UUID networkId,
                            @ApiParameter(id = "device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "revoke")
    public ApiResponse revoke(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "network_id") final UUID networkId,
                              @ApiParameter(id = "device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(HttpResponseStatus.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "invite")
    public ApiResponse invite(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
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

        if (device == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "DEVICE_NOT_FOUND");
        }

        if (this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(HttpResponseStatus.OK, this.networkInvitationRepository.create(network, device, network.getOwner()));
        } else if (invitation.isRequest()) {
            this.networkInvitationRepository.delete(invitation);
            return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
        } else {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "INVITATION_ALREADY_EXISTS");
        }
    }

    @ApiEndpoint(id = "request")
    public ApiResponse request(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                               @ApiParameter(id = "device_id") final UUID deviceId,
                               @ApiParameter(id = "network_id") final UUID networkId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);

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

        if (this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).isPresent()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(HttpResponseStatus.OK, this.networkInvitationRepository.create(network, device, null));
        } else if (invitation.isRequest()) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "REQUEST_ALREADY_EXISTS");
        } else {
            this.networkInvitationRepository.delete(invitation);
            return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
        }
    }

}
