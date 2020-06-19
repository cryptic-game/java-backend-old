package net.cryptic_game.backend.endpoints.network;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkOwnerEndpoints extends ApiEndpointCollection {

    public NetworkOwnerEndpoints() {
        super("network/owner");
    }

    @ApiEndpoint("owner")
    public ApiResponse getAll(@ApiParameter("device") final UUID deviceId) {
        Device device = Device.getById(deviceId);
        if (device == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");

        if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        List<Network> networks = Network.getNetworksOwnedByDevice(device);

        List<JsonObject> json = new ArrayList<>();
        networks.forEach(n -> json.add(n.serialize()));

        return new ApiResponse(ApiResponseType.NOT_IMPLEMENTED, JsonBuilder.create("networks", json));
    }

    @ApiEndpoint("invite")
    public ApiResponse invite(@ApiParameter("user_id") UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {

        Device device = Device.getById(deviceId);
        if (device == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");

        if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        Network network = Network.getById(networkId);
        User user = User.getById(userId);

        if (network == null || !device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK_NOT_FOUND");

        if (network.getOwner().equals(device)) return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");

        NetworkMember member = NetworkMember.getMember(network, device);
        if (member != null) return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");

        List<NetworkInvitation> invitations = new ArrayList<>();
        for (NetworkInvitation invitation : NetworkInvitation.getInvitationsOfNetwork(network)) {
            invitations.add(invitation);
        }
        if (invitations.stream().anyMatch(inv -> inv.getNetwork().getId().equals(networkId)))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "INVITATION_ALREADY_EXISTS");

        Device inviter = NetworkInvitation.getInvitation(network, device).getDevice();
        NetworkInvitation networkInvitation = NetworkInvitation.createInvitation(network, device, inviter);

        return new ApiResponse(ApiResponseType.OK, networkInvitation.serialize());
    }

    @ApiEndpoint("accept")
    public ApiResponse accept(@ApiParameter("user") final UUID userId,
                              @ApiParameter("uuid") final UUID invitationId) {
        NetworkInvitation invitation = NetworkInvitation.getById(invitationId);
        User user = User.getById(userId);
        if (invitation == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION_NOT_FOUND");

        if (!invitation.isRequest()) {
            Device device = invitation.getDevice();

            if (!device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        } else {
            Device owner = invitation.getNetwork().getOwner();

            if (!owner.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!owner.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        }

        invitation.accept();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("deny")
    public ApiResponse deny(@ApiParameter("user") final UUID userId,
                            @ApiParameter("uuid") final UUID invitationId) {
        NetworkInvitation invitation = NetworkInvitation.getById(invitationId);
        User user = User.getById(userId);
        if (invitation == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION_NOT_FOUND");

        if (!invitation.isRequest()) {
            Device device = invitation.getDevice();

            if (!device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        } else {
            Device owner = invitation.getNetwork().getOwner();

            if (!owner.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!owner.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        }

        invitation.deny();

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("requests")
    public ApiResponse requests(@ApiParameter("user_id") final UUID userId,
                                @ApiParameter("network_id") final UUID networkId) {

        Network network = Network.getById(networkId);
        User user = User.getById(userId);

        if (network == null || !network.getOwner().hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        List<NetworkInvitation> invitations = new ArrayList<>(NetworkInvitation.getInvitationsOfNetwork(network));

        return new ApiResponse(ApiResponseType.OK, invitations);
    }

    @ApiEndpoint("kick")
    public ApiResponse kick(@ApiParameter("user_id") final UUID userId,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device") final UUID deviceId) {

        User user = User.getById(userId);
        Network network = Network.getById(networkId);
        Device device = Device.getById(deviceId);

        if (network == null || !network.getOwner().hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

        if (!network.getOwner().isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        if (network.getOwner().equals(device)) return new ApiResponse(ApiResponseType.FORBIDDEN, "CANNOT_KICK_OWNER");

        for (NetworkMember member : NetworkMember.getMembershipsOfNetwork(network)) {
            if (member.getDevice().equals(device)) {
                member.delete();

                return new ApiResponse(ApiResponseType.OK);
            }
        }

        return new ApiResponse(ApiResponseType.NOT_FOUND, "USER_NOT_FOUND");
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter("user") final UUID userId,
                              @ApiParameter("uuid") final UUID networkId) {

        User user = User.getById(userId);
        Network network = Network.getById(networkId);

        if (network == null || !network.getOwner().hasUserAccess(user)) return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK_NOT_FOUND");

        if (network.getOwner().isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        network.delete();

        List<NetworkMember> members = NetworkMember.getMembershipsOfNetwork(network);
        members.forEach(TableModel::delete);

        List<NetworkInvitation> invitations = NetworkInvitation.getInvitationsOfNetwork(network);
        invitations.forEach(TableModel::delete);

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("revoke")
    public ApiResponse revoke(@ApiParameter("user") final UUID userId,
                              @ApiParameter("uuid") final UUID invitationId) {
        NetworkInvitation invitation = NetworkInvitation.getById(invitationId);
        User user = User.getById(userId);
        if (invitation == null) return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION_NOT_FOUND");

        if (!invitation.isRequest()) {
            Device device = invitation.getDevice();

            if (!device.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!device.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        } else {
            Device owner = invitation.getNetwork().getOwner();

            if (!owner.hasUserAccess(user)) return new ApiResponse(ApiResponseType.FORBIDDEN, "NO_PERMISSION");

            if (!owner.isPoweredOn()) return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        }

        // TODO: revoke invitation
        // Create new one??

        return new ApiResponse(ApiResponseType.OK);
    }
}
