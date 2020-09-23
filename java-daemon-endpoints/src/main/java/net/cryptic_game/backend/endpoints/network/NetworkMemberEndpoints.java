package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
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
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class NetworkMemberEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;
    private final NetworkRepository networkRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final NetworkInvitationRepository networkInvitationRepository;

    public NetworkMemberEndpoints(final UserRepository userRepository,
                                  final DeviceRepository deviceRepository,
                                  final DeviceAccessRepository deviceAccessRepository,
                                  final NetworkRepository networkRepository,
                                  final NetworkMemberRepository networkMemberRepository,
                                  final NetworkInvitationRepository networkInvitationRepository) {
        super("network/member", "todo");
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.deviceAccessRepository = deviceAccessRepository;
        this.networkRepository = networkRepository;
        this.networkMemberRepository = networkMemberRepository;
        this.networkInvitationRepository = networkInvitationRepository;
    }

    @ApiEndpoint("members")
    public ApiResponse member(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = userRepository.findById(userId).orElse(null);
        final Device device = deviceRepository.findById(deviceId).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, networkMemberRepository.findAllByKeyDevice(device));
    }

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("device_id") final UUID deviceId) {
        final User user = userRepository.findById(userId).orElse(null);
        final Device device = deviceRepository.findById(deviceId).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, networkInvitationRepository.findAllByKeyDevice(device));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter("device_id") final UUID deviceId,
                             @ApiParameter("network_id") final UUID id) {
        final User user = userRepository.findById(userId).orElse(null);
        final Device device = deviceRepository.findById(deviceId).orElse(null);
        final Network network = networkRepository.findById(id).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "CAN_NOT_LEAVE_OWN_NETWORK");
        }

        final NetworkMember member = networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).orElse(null);
        if (member == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }
        networkMemberRepository.delete(member);
        return new ApiResponse(ApiResponseType.OK);
    }
}
