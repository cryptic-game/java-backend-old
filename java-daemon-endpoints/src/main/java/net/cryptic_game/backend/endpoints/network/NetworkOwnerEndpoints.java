package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.entities.device.Device;
import net.cryptic_game.backend.data.entities.network.Network;
import net.cryptic_game.backend.data.entities.network.NetworkInvitation;
import net.cryptic_game.backend.data.entities.network.NetworkMember;
import net.cryptic_game.backend.data.entities.user.User;
import org.hibernate.Session;

import java.util.UUID;

public final class NetworkOwnerEndpoints extends ApiEndpointCollection {

    public NetworkOwnerEndpoints() {
        super("network/owner", "todo");
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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

        return new ApiResponse(ApiResponseType.OK, Network.getNetworksOwnedByDevice(session, device));
    }

    @ApiEndpoint("invitations")
    public ApiResponse invitations(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                                   @ApiParameter("network_id") final UUID networkId) {
        final Network network = Network.getById(session, networkId);
        final User user = User.getById(session, userId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkInvitation.getInvitationsOfNetwork(session, network));
    }

    @ApiEndpoint("kick")
    public ApiResponse kick(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
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

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        if (network.getOwner().equals(device)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "CAN_NOT_KICK_OWNER");
        }

        if (device == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "DEVICE");
        }

        final NetworkMember networkMember = NetworkMember.getMember(session, network, device);

        if (networkMember == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MEMBER");
        }

        networkMember.delete(session);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("network_id") final UUID networkId) {
        User user = User.getById(session, userId);
        Network network = Network.getById(session, networkId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        if (!network.getOwner().hasAccess(session, user)) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "ACCESS_DENIED");
        }

        if (!network.getOwner().isPoweredOn()) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");
        }

        NetworkMember.getMembershipsOfNetwork(session, network).forEach(i -> i.delete(session));
        NetworkInvitation.getInvitationsOfNetwork(session, network).forEach(i -> i.delete(session));
        network.delete(session);

        return new ApiResponse(ApiResponseType.OK);
    }
}
