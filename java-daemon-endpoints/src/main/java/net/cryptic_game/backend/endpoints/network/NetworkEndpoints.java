/** package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonArray;
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
    public ApiResponse create(@ApiParameter("user_id") UUID userId,
                              @ApiParameter("name") String name,
                              @ApiParameter("hidden") boolean hidden,
                              @ApiParameter("device") UUID deviceId) {
        User user = User.getById(userId);
        Device device = Device.getById(deviceId);

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
    public ApiResponse list(@ApiParameter("user_id") UUID userId,
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
} **/

package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonObject;
import com.google.protobuf.Api;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;
import org.h2.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkEndpoint extends ApiEndpointCollection {

    public NetworkEndpoint() {
        super("network");
    }

    @ApiEndpoint("names")
    public ApiResponse name(@ApiParameter("user_id") UUID userId,
                            @ApiParameter("name") String name) {

        Network network = Network.getByName(name);
        if (network == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "network_not_found");

        return new ApiResponse(ApiResponseType.OK, network.serialize());
    }

    @ApiEndpoint("gets")
    public ApiResponse get(@ApiParameter("user_id") UUID userId,
                           @ApiParameter("uuid") UUID uuid) {

        Network network = Network.getById(uuid);
        if (network == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "network_not_found");

        return new ApiResponse(ApiResponseType.OK, network.serialize());
    }

    @ApiEndpoint("publics")
    public ApiResponse GetPublic(@ApiParameter("user_id") UUID userId) {

        List<Network> networks = Network.getPublicNetworks();

        List<JsonObject> jsonNetworks = new ArrayList<>();

        for (Network network : networks) {
            jsonNetworks.add(network.serialize());
        }

        return new ApiResponse(ApiResponseType.OK, jsonNetworks);

    }

    @ApiEndpoint("creates")
    public ApiResponse create(@ApiParameter("user_id") UUID userId,
                              @ApiParameter("device") UUID deviceId,
                              @ApiParameter("name") String name,
                              @ApiParameter("hidden") Boolean hidden) {

        User user = User.getById(userId);
        Device device = Device.getById(deviceId);

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

    @ApiEndpoint("checks")
    public ApiResponse check(@ApiParameter("user_id") UUID userId,
                             @ApiParameter("source") UUID sourceId,
                             @ApiParameter("destination") UUID destinationId) {

        //TODO get Member class
        for (Network network : Member.getNetworks(sourceId)) {
            for (Member member : Member.getMembers(network.getUUID())) {
                if (member.getDevice().equals(destinationId)) {
                    return new ApiResponse(ApiResponseType.OK, "connected");
                }
            }
        }

        return new ApiResponse(ApiResponseType.FORBIDDEN, "connected");

    }

    @ApiEndpoint("members")
    public ApiResponse members(@ApiParameter("user_id") UUID userId,
                               @ApiParameter("uuid") UUID uuid,
                               @ApiParameter("device") UUID deviceId) {

        Network network = Network.getById(uuid);
        User user = User.getById(userId);
        Device device = Device.getById(deviceId);

        return new ApiResponse(ApiResponseType.FORBIDDEN, "connected");

        if (network == null || !device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "network_not_found");
        }

        //TODO get Member class
        List<Member> members = Member.getMembers(uuid);
        List<JSONObject> jsonMembers = new ArrayList<>();

        for (Member member : members) {
            jsonMembers.add(member.serialize());
        }

        return new ApiResponse(ApiResponseType.OK, jsonMembers);

    }

    @ApiEndpoint("delete_users")
    public ApiResponse deleteUser(@ApiParameter("user_id") UUID userId,
                             @ApiParameter("ms") String ms) {

        // TODO: get Invitation and Member classes
        for (Invitation invitation : Invitation.getInvitationsOfUser(user)) {
            invitation.delete();
        }
        for (Member member : Member.getMembershipsOfUser(user)) {
            member.delete();
        }
        for (Network network : Network.getNetworksOfUser(user)) {
            for (Member member : Member.getMembers(network.getUUID())) {
                member.delete();
            }
            network.delete();
        }

        return new ApiResponse(ApiResponseType.OK);

    }

}
