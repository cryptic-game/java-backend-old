package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonArray;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network", "todo");
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) UUID userId,
                              @ApiParameter("name") String name,
                              @ApiParameter("hidden") boolean hidden,
                              @ApiParameter("device") UUID deviceId) {
        User user = User.getById(userId);
        Device device = Device.getById(deviceId);

        if (device == null)
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");

        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        if (Network.getNetworksOwnedByDevice(device).size() >= 2)
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");

        if (Network.getByName(name) != null)
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");

        Network network = Network.createNetwork(name, device, hidden);
        NetworkMember.createMember(network, device);

        return new ApiResponse(ApiResponseType.OK, network.serialize());
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) UUID userId,
                            @ApiParameter(value = "device", optional = true) UUID deviceId) {
        if (deviceId == null) {
            JsonArray networks = new JsonArray();
            Network.getPublicNetworks().forEach(network -> networks.add(network.serialize()));
            return new ApiResponse(ApiResponseType.OK, networks);
        }

        User user = User.getById(userId);
        Device device = Device.getById(deviceId);

        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        JsonArray networks = new JsonArray();
        NetworkMember.getMembershipsOfDevice(device).forEach(networkMember -> networks.add(networkMember.serialize()));
        return new ApiResponse(ApiResponseType.OK, networks);
    }
}
