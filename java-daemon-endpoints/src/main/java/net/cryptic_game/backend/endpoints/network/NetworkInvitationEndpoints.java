package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkInvitation;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.UUID;

public final class NetworkInvitationEndpoints extends ApiEndpointCollection {

    public NetworkInvitationEndpoints() {
        super("network/invitation", "todo");
    }

    @ApiEndpoint("accept")
    public ApiResponse accept(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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
            if (!invitation.getNetwork().getOwner().hasAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasAccess(user)) {
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
    public ApiResponse deny(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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
            if (!invitation.getNetwork().getOwner().hasAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasAccess(user)) {
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
    public ApiResponse revoke(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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
            if (!device.hasAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!invitation.getNetwork().getOwner().hasAccess(user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("invite")
    public ApiResponse invite(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(userId);
        final Network network = Network.getById(networkId);
        final Device device = Device.getById(deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasAccess(user)) {
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

    @ApiEndpoint("request")
    public ApiResponse request(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter("device_id") final UUID deviceId,
                               @ApiParameter("network_id") final UUID networkId) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);
        final Network network = Network.getById(networkId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK");
        }

        if (NetworkMember.getMember(network, device) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, NetworkInvitation.createInvitation(network, device, null));
        } else if (invitation.isRequest()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "REQUEST_ALREADY_EXISTS");
        } else {
            invitation.delete();
            return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(invitation.getNetwork(), invitation.getDevice()));
        }
    }

}
