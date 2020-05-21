package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkMemberEndpoints extends ApiEndpointCollection {

    public NetworkMemberEndpoints() {
        super("network_member");
    }

    @ApiEndpoint("member")
    public ApiResponse member(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("device") final UUID deviceId) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);

        if (device == null || !device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");
        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        List<NetworkMember> memberships = NetworkMember.getMembershipsOfDevice(device);

        return new ApiResponse(ApiResponseType.OK, memberships);
    }

    @ApiEndpoint("request")
    public Object request(@ApiParameter("user") final UUID userId,
                          @ApiParameter("device") final UUID deviceId,
                          @ApiParameter("id") final UUID id) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);
        Network network = Network.getById(id);

        if (device == null || !device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_NOT_FOUND");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_MEMBER_OF_NETWORK");
        }

        if (NetworkMember.getMember(network, device) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        if (NetworkInvitation.getInvitation(network, device) != null) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "INVITATION_ALREADY_EXISTS");
        }

        NetworkInvitation invitation = NetworkInvitation.createInvitation(network, device, null);

        return new ApiResponse(ApiResponseType.OK, invitation.serialize());
    }

    @ApiEndpoint("invitations")
    public Object invitations(@ApiParameter("user") final UUID userId,
                              @ApiParameter("device") final UUID deviceId,
                              @ApiParameter("id") final UUID id) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);
        Network network = Network.getById(id);

        if (device == null || !device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        List<JsonObject> invitations = new ArrayList<>();
        List<NetworkInvitation> networkInvitations = (List<NetworkInvitation>) NetworkInvitation.getInvitation(network, device);

        for (NetworkInvitation invitation : networkInvitations) {
            invitations.add(invitation.serialize());
        }

        return new ApiResponse(ApiResponseType.OK, invitations);
    }

    @ApiEndpoint("leave")
    public Object leave(@ApiParameter("user") final UUID userId,
                        @ApiParameter("device") final UUID deviceId,
                        @ApiParameter("id") final UUID id) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);
        Network network = Network.getById(id);

        if (device == null || !device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "CANNOT_LEAVE_OWN_NETWORK");
        }

        NetworkMember member = NetworkMember.getMember(network, device);
        if (member != null) {
            member.delete();
            return new ApiResponse(ApiResponseType.OK);
        }

        return new ApiResponse(ApiResponseType.BAD_REQUEST);  // I dont know witch request message i should write for this
    }
}
