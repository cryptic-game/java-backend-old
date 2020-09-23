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
public final class NetworkOwnerEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;
    private final NetworkRepository networkRepository;
    private final NetworkInvitationRepository networkInvitationRepository;
    private final NetworkMemberRepository networkMemberRepository;

    public NetworkOwnerEndpoints(final UserRepository userRepository,
                                 final DeviceRepository deviceRepository,
                                 final DeviceAccessRepository deviceAccessRepository,
                                 final NetworkRepository networkRepository,
                                 final NetworkInvitationRepository networkInvitationRepository,
                                 final NetworkMemberRepository networkMemberRepository) {
        super("network/owner", "todo");
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.deviceAccessRepository = deviceAccessRepository;
        this.networkRepository = networkRepository;
        this.networkInvitationRepository = networkInvitationRepository;
        this.networkMemberRepository = networkMemberRepository;
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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

        return new ApiResponse(ApiResponseType.OK, networkRepository.findAllByOwner(device));
    }

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("network_id") final UUID networkId) {
        final Network network = networkRepository.findById(networkId).orElse(null);
        final User user = userRepository.findById(userId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, networkInvitationRepository.findAllByKeyNetwork(network));
    }

    @ApiEndpoint("kick")
    public ApiResponse kick(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = userRepository.findById(userId).orElse(null);
        final Network network = networkRepository.findById(networkId).orElse(null);
        final Device device = deviceRepository.findById(deviceId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "CAN_NOT_KICK_OWNER");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkMember networkMember = networkMemberRepository.findByKeyDeviceAndKeyNetwork(device, network).orElse(null);

        if (networkMember == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }

        networkMemberRepository.delete(networkMember);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("network_id") final UUID networkId) {
        User user = userRepository.findById(userId).orElse(null);
        Network network = networkRepository.findById(networkId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!deviceAccessRepository.hasAccess(network.getOwner(), user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        networkMemberRepository.deleteAllByKeyNetwork(network);
        networkInvitationRepository.deleteAllByKeyNetwork(network);
        networkRepository.delete(network);

        return new ApiResponse(ApiResponseType.OK);
    }
}
