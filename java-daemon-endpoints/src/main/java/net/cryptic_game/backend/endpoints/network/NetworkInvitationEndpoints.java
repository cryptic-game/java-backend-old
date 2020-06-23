package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public class NetworkInvitationEndpoints extends ApiEndpointCollection {

    public NetworkInvitationEndpoints() {
        super("network/invitation", "todo");
    }

    @ApiEndpoint("accept")
    public ApiResponse accept(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!invitation.getNetwork().getOwner().hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete();
        return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(invitation.getNetwork(), invitation.getDevice()));
    }


    @ApiEndpoint("deny")
    public ApiResponse deny(@ApiParameter("user_id") final UUID userId,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!invitation.getNetwork().getOwner().hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("revoke")
    public ApiResponse revoke(@ApiParameter("user_id") final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!device.hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!invitation.getNetwork().getOwner().hasUserAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete();
        return new ApiResponse(ApiResponseType.OK);
    }
}
