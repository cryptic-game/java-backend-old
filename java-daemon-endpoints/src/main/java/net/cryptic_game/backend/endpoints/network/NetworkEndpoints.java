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
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.device.DeviceRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkMemberRepository;
import net.cryptic_game.backend.data.sql.repositories.network.NetworkRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "network", type = ApiType.REST, authenticator = DaemonAuthenticator.class)
public final class NetworkEndpoints {

    private final NetworkRepository networkRepository;
    private final NetworkMemberRepository networkMemberRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceAccessRepository deviceAccessRepository;

    @ApiEndpoint(id = "get")
    public ApiResponse get(@ApiParameter(id = "network_id", required = false) final UUID networkId,
                           @ApiParameter(id = "name", required = false) final String name) {
        if (networkId == null && name == null) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NO_NAME_OR_NETWORK_ID_PROVIDED");
        }

        final Network network = (networkId == null ? this.networkRepository.findByName(name) : this.networkRepository.findById(networkId)).orElse(null);
        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(HttpResponseStatus.OK, network);
    }

    @ApiEndpoint(id = "public")
    public ApiResponse getPublic() {
        return new ApiResponse(HttpResponseStatus.OK, this.networkRepository.findAllByIsPublicTrue());
    }

    @ApiEndpoint(id = "create")
    public ApiResponse create(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "device_id") final UUID deviceId,
                              @ApiParameter(id = "name") final String name,
                              @ApiParameter(id = "public") final boolean isPublic) {
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

        if (this.networkRepository.findAllByOwner(device).size() >= 2) {
            return new ApiResponse(HttpResponseStatus.FORBIDDEN, "NETWORK_LIMIT_REACHED");
        }

        if (this.networkRepository.findByName(name).isPresent()) {
            return new ApiResponse(HttpResponseStatus.CONFLICT, "NETWORK_NAME_ALREADY_EXISTS");
        }

        final Network network = this.networkRepository.create(name, device, isPublic);
        this.networkMemberRepository.create(network, device);
        return new ApiResponse(HttpResponseStatus.OK, network);
    }

    @ApiEndpoint(id = "members")
    public ApiResponse members(@ApiParameter(id = "network_id") final UUID networkId) {
        final Network network = this.networkRepository.findById(networkId).orElse(null);

        if (network == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.findAllByKeyNetwork(network));
    }

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

        return new ApiResponse(HttpResponseStatus.OK, this.networkMemberRepository.findAllByKeyDevice(device));
    }
}
