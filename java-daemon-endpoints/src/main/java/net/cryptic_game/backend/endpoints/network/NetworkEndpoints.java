package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network");
    }

    @ApiEndpoint("create")
    public ApiResponse create(JsonObject userJson,
                              @ApiParameter("name") String name,
                              @ApiParameter("hidden") boolean hidden,
                              @ApiParameter("device") UUID deviceUUID) {
        User user = new User(userJson);
        Device device = Device.getById(deviceUUID);

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
}
