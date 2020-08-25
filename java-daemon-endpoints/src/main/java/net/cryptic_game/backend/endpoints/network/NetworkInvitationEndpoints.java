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
import org.hibernate.Session;

import java.util.UUID;

public final class NetworkInvitationEndpoints extends ApiEndpointCollection {

    public NetworkInvitationEndpoints() {
        super("network/invitation", "todo");
    }

    @ApiEndpoint("accept")
    public ApiResponse accept(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Network network = Network.getById(session, networkId);
        final Device device = Device.getById(session, deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(session, network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!invitation.getNetwork().getOwner().hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete(session);
        return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(session, invitation.getNetwork(), invitation.getDevice()));
    }


    @ApiEndpoint("deny")
    public ApiResponse deny(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                            @ApiParameter("network_id") final UUID networkId,
                            @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Network network = Network.getById(session, networkId);
        final Device device = Device.getById(session, deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(session, network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!invitation.getNetwork().getOwner().hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!device.hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete(session);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("revoke")
    public ApiResponse revoke(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Network network = Network.getById(session, networkId);
        final Device device = Device.getById(session, deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(session, network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "INVITATION");
        }

        if (invitation.isRequest()) {
            if (!device.hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!device.isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        } else {
            if (!invitation.getNetwork().getOwner().hasAccess(session, user)) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
            }

            if (!invitation.getNetwork().getOwner().isPoweredOn()) {
                return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
            }
        }

        invitation.delete(session);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("invite")
    public ApiResponse invite(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("network_id") final UUID networkId,
                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Network network = Network.getById(session, networkId);
        final Device device = Device.getById(session, deviceId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE_NOT_FOUND");
        }

        if (NetworkMember.getMember(session, network, device) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(session, network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, NetworkInvitation.createInvitation(session, network, device, network.getOwner()));
        } else if (invitation.isRequest()) {
            invitation.delete(session);
            return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(session, invitation.getNetwork(), invitation.getDevice()));
        } else {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "INVITATION_ALREADY_EXISTS");
        }
    }

    @ApiEndpoint("request")
    public ApiResponse request(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,

                               @ApiParameter("device_id") final UUID deviceId,
                               @ApiParameter("network_id") final UUID networkId) {
        final User user = User.getById(session, userId);
        final Device device = Device.getById(session, deviceId);
        final Network network = Network.getById(session, networkId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network == null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK");
        }

        if (NetworkMember.getMember(session, network, device) != null) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ALREADY_MEMBER_OF_NETWORK");
        }

        final NetworkInvitation invitation = NetworkInvitation.getInvitation(session, network, device);
        if (invitation == null) {
            return new ApiResponse(ApiResponseType.OK, NetworkInvitation.createInvitation(session, network, device, null));
        } else if (invitation.isRequest()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "REQUEST_ALREADY_EXISTS");
        } else {
            invitation.delete(session);
            return new ApiResponse(ApiResponseType.OK, NetworkMember.createMember(session, invitation.getNetwork(), invitation.getDevice()));
        }
    }

}
