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

public class NetworkMemberEndpoints extends ApiEndpointCollection {

    public NetworkMemberEndpoints() {
        super("network/member", "todo");
    }

    @ApiEndpoint("members")
    public ApiResponse member(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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

        if (!device.hasUserAccess(user)) {
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

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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

        return new ApiResponse(ApiResponseType.OK, NetworkInvitation.getInvitationsOfDevice(device));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter("device_id") final UUID deviceId,
                             @ApiParameter("network_id") final UUID id) {
        final User user = User.getById(userId);
        final Device device = Device.getById(deviceId);
        final Network network = Network.getById(id);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasUserAccess(user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "CAN_NOT_LEAVE_OWN_NETWORK");
        }

        final NetworkMember member = NetworkMember.getMember(network, device);
        if (member == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }
        member.delete();
        return new ApiResponse(ApiResponseType.OK);
    }
}
