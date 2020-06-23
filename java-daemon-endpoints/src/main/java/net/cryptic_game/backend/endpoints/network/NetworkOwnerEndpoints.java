package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkOwnerEndpoints extends ApiEndpointCollection {

    public NetworkOwnerEndpoints() {
        super("network/owner");
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter("user_id") final UUID userId,
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

        return new ApiResponse(ApiResponseType.OK, Network.getNetworksOwnedByDevice(device));
    }

    @ApiEndpoint("invite")
    public ApiResponse invite(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");
        }

        if (NetworkMember.getMember(network, device) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, NetworkInvitation.createInvitation(network, device, network.getOwner()));
        } else if (invitation.isRequest()) {
            invitation.delete();
            return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(invitation.getNetwork(), invitation.getDevice()));
        } else {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "INVITATION_ALREADY_EXISTS");
        }
    }

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter("user_id") final UUID userId,
                                   @ApiParameter("network_id") final UUID networkId) {
        final Network network = Network.getById(networkId);
        final User user = User.getById(userId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkInvitation.getInvitationsOfNetwork(network));
    }

    @ApiEndpoint("kick")
    public ApiResponse kick(@ApiParameter("user_id") final UUID userId,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasUserAccess(user)) {
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

        final NetworkMember networkMember = NetworkMember.getMember(network, device);

        if (networkMember == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }

        networkMember.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("network_id") final UUID networkId) {
        User user = User.getById(userId);
        Network network = Network.getById(networkId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        NetworkMember.getMembershipsOfNetwork(network).forEach(TableModel::delete);
        NetworkInvitation.getInvitationsOfNetwork(network).forEach(TableModel::delete);
        network.delete();

        return new ApiResponse(ApiResponseType.OK);
    }
}
