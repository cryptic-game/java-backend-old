package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.sql.entities.device.Device;
import net.cryptic_game.backend.data.sql.entities.network.Network;
import net.cryptic_game.backend.data.sql.entities.network.NetworkInvitation;
import net.cryptic_game.backend.data.sql.entities.network.NetworkMember;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.hibernate.Session;

import java.util.UUID;

public final class NetworkMemberEndpoints extends ApiEndpointCollection {

    public NetworkMemberEndpoints() {
        super("network/member", "todo");
    }

    @ApiEndpoint("members")
    public ApiResponse member(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,

                              @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Device device = Device.getById(session, deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfDevice(session, device));
    }

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                                   @ApiParameter("device_id") final UUID deviceId) {
        final User user = User.getById(session, userId);
        final Device device = Device.getById(session, deviceId);

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        if (!device.hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!device.isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkInvitation.getInvitationsOfDevice(session, device));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                             @ApiParameter("device_id") final UUID deviceId,
                             @ApiParameter("network_id") final UUID id) {
        final User user = User.getById(session, userId);
        final Device device = Device.getById(session, deviceId);
        final Network network = Network.getById(session, id);

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

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "CAN_NOT_LEAVE_OWN_NETWORK");
        }

        final NetworkMember member = NetworkMember.getMember(session, network, device);
        if (member == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }
        member.delete(session);
        return new ApiResponse(ApiResponseType.OK);
    }
}
