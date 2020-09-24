package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkMemberRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class NetworkEndpoints extends ApiEndpointCollection {

    private final NetworkRepository networkRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;

    public NetworkEndpoints(final NetworkRepository networkRepository,
                            final NetworkMemberRepository networkMemberRepository,
                            final UserRepository userRepository,
                            final DeviceRepository deviceRepository,
                            final DeviceAccessRepository deviceAccessRepository) {
        super("network", "todo");
        this.networkRepository = networkRepository;
        this.networkMemberRepository = networkMemberRepository;
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.deviceAccessRepository = deviceAccessRepository;
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "network_id", optional = true) final UUID networkId,
                           @ApiParameter(value = "name", optional = true) final String name) {
        if (networkId == null && name == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NO_NAME_OR_NETWORK_ID_PROVIDED");
        }

        final Network network = (networkId == null ? this.networkRepository.findByName(name) : this.networkRepository.findById(networkId)).orElse(null);
        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("public")
    public ApiResponse getPublic() {
        return new ApiResponse(ApiResponseType.OK, this.networkRepository.findAllByIsPublicTrue());
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("device_id") final UUID deviceId,
                              @ApiParameter("name") final String name,
                              @ApiParameter("public") final boolean isPublic) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!this.deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (this.networkRepository.findAllByOwner(device).size() >= 2) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");
        }

        if (this.networkRepository.findByName(name).isPresent()) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");
        }

        final Network network = this.networkRepository.create(name, device, isPublic);
        this.networkMemberRepository.create(network, device);
        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("members")
    public ApiResponse members(@ApiParameter("network_id") final UUID networkId) {
        final Network network = this.networkRepository.findById(networkId).orElse(null);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, this.networkMemberRepository.findAllByKeyNetwork(network));
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final Device device = this.deviceRepository.findById(deviceId).orElse(null);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!this.deviceAccessRepository.hasAccess(device, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, this.networkMemberRepository.findAllByKeyDevice(device));
    }
}
