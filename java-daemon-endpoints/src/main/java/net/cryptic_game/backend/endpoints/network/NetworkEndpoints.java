package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network", "todo");
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "network_id", optional = true) final UUID networkId,
                           @ApiParameter(value = "name", optional = true) final String name) {
        if (networkId == null && name == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NO_NAME_OR_NETWORK_ID_PROVIDED");
        }

        final Network network = networkId == null ? Network.getByName(name) : Network.getById(networkId);
        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("public")
    public ApiResponse getPublic() {
        return new ApiResponse(ApiResponseType.OK, Network.getPublicNetworks());
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("device_id") final UUID deviceId,
                              @ApiParameter("name") final String name,
                              @ApiParameter("public") final boolean _public) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (Network.getNetworksOwnedByDevice(device).size() >= 2) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");
        }

        if (Network.getByName(name) != null) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");
        }

        final Network network = Network.createNetwork(name, device, _public);
        NetworkMember.createMember(network, device);
        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("members")
    public ApiResponse members(@ApiParameter("network_id") final UUID networkId) {
        final Network network = Network.getById(networkId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfNetwork(network));
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfDevice(device));
    }
}
