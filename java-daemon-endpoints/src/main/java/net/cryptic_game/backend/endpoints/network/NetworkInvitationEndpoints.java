package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
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
public final class NetworkInvitationEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final NetworkRepository networkRepository;
    private final NetworkInvitationRepository networkInvitationRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;

    public NetworkInvitationEndpoints(final UserRepository userRepository,
                                      final NetworkRepository networkRepository,
                                      final NetworkMemberRepository networkMemberRepository,
                                      final DeviceRepository deviceRepository,
                                      final NetworkInvitationRepository networkInvitationRepository,
                                      final DeviceAccessRepository deviceAccessRepository) {
        super("network/invitation", "todo");
        this.userRepository = userRepository;
        this.networkRepository = networkRepository;
        this.networkMemberRepository = networkMemberRepository;
        this.deviceRepository = deviceRepository;
        this.networkInvitationRepository = networkInvitationRepository;
        this.deviceAccessRepository = deviceAccessRepository;
    }

    @ApiEndpoint("accept")
    public ApiResponse accept(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(ApiResponseType.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
    }


    @ApiEndpoint("deny")
    public ApiResponse deny(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("revoke")
    public ApiResponse revoke(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!this.deviceAccessRepository.hasAccess(device, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!this.deviceAccessRepository.hasAccess(invitation.getNetwork().getOwner(), user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        this.networkInvitationRepository.delete(invitation);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("invite")
    public ApiResponse invite(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!this.deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");
        }

        if (this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).isPresent()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, this.networkInvitationRepository.create(network, device, network.getOwner()));
        } else if (invitation.isRequest()) {
            this.networkInvitationRepository.delete(invitation);
            return new ApiResponse(ApiResponseType.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
        } else {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "INVITATION_ALREADY_EXISTS");
        }
    }

    @ApiEndpoint("request")
    public ApiResponse request(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter("device_id") final UUID deviceId,
                               @ApiParameter("network_id") final UUID networkId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);
        final Network network = this.networkRepository.findById(networkId).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!this.deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK");
        }

        if (this.networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).isPresent()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = this.networkInvitationRepository.findByKeyNetworkAndKeyDevice(network, device).orElse(null);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, this.networkInvitationRepository.create(network, device, null));
        } else if (invitation.isRequest()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "REQUEST_ALREADY_EXISTS");
        } else {
            this.networkInvitationRepository.delete(invitation);
            return new ApiResponse(ApiResponseType.OK, this.networkMemberRepository.create(invitation.getNetwork(), invitation.getDevice()));
        }
    }

}
