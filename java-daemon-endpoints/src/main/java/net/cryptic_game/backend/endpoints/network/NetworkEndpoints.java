package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonArray;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;
import org.h2.util.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network");
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "user_id") final UUID userId,
                           @ApiParameter(value = "network_id", optional = true) final UUID network_id,
                           @ApiParameter(value = "name", optional = true) final String name) {

        if (network_id == null && name == null) return new ApiResponse(ApiResponseType.BAD_REQUEST, "NO_NAME_OR_NETWORKID_PROVIDED");
        Network network;
        if(network_id != null) {
            network = Network.getById(network_id);
        } else {
            network = Network.getByName(name);
        }
        if (network == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK_NOT_FOUND");

        return new ApiResponse(ApiResponseType.OK, network.serialize());

    }

    @ApiEndpoint("public")
    public ApiResponse GetPublic(@ApiParameter("user_id") final UUID userId) {

        final List<Network> networks = Network.getPublicNetworks();

        return new ApiResponse(ApiResponseType.OK, networks);

    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("device_id") final UUID deviceId,
                              @ApiParameter("name") final String name,
                              @ApiParameter("hidden") final Boolean hidden) {

        final User user = User.getById(userId);
        if (user == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
        Device device = Device.getById(deviceId);
        if (device == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");

        if (!device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        if (Network.getNetworksOwnedByDevice(device).size() >= 2) return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");

        if (Network.getByName(name) != null) return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");

        final Network network = Network.createNetwork(name, device, hidden);
        NetworkMember.createMember(network, device);

        return new ApiResponse(ApiResponseType.OK, network);

    }

    @ApiEndpoint("member")
    public ApiResponse members(@ApiParameter("user_id") final UUID userId,
                               @ApiParameter("network_id") final UUID network_id,
                               @ApiParameter("device") final UUID deviceId) {

        final Network network = Network.getById(network_id);
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (network == null || !device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK_NOT_FOUND");
        }

        //TODO add or find Methode getMembers
        final List<NetworkMember> members = NetworkMember.getMembershipsOfNetwork(network);

        return new ApiResponse(ApiResponseType.OK, members);

    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter("user_id") final UUID userId,
                            @ApiParameter(value = "device", optional = true) final UUID deviceId) {
        if (deviceId == null) {
            JsonArray networks = new JsonArray();
            Network.getPublicNetworks().forEach(network -> networks.add(network.serialize()));
            return new ApiResponse(ApiResponseType.OK, networks);
        }

        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        final JsonArray networks = new JsonArray();
        NetworkMember.getMembershipsOfDevice(device).forEach(networkMember -> networks.add(networkMember.serialize()));
        return new ApiResponse(ApiResponseType.OK, networks);
    }

}
