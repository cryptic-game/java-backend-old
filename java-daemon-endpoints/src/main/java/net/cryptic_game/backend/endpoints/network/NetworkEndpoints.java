package net.cryptic_game.backend.endpoints.network;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;
import org.hibernate.Session;

import java.util.UUID;

public final class NetworkEndpoints extends ApiEndpointCollection {

    public NetworkEndpoints() {
        super("network", "todo");
    }

    @ApiEndpoint("get")
    public ApiResponse get(@ApiParameter(value = "network_id", optional = true) final UUID networkId,
                           @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,

                           @ApiParameter(value = "name", optional = true) final String name) {
        if (networkId == null && name == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NO_NAME_OR_NETWORK_ID_PROVIDED");
        }

        final Network network = networkId == null ? Network.getByName(session, name) : Network.getById(session, networkId);
        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("public")
    public ApiResponse getPublic(@ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session) {
        return new ApiResponse(ApiResponseType.OK, Network.getPublicNetworks(session));
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("device_id") final UUID deviceId,
                              @ApiParameter("name") final String name,
                              @ApiParameter("public") final boolean isPublic) {
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

        if (Network.getNetworksOwnedByDevice(session, device).size() >= 2) {
            return new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_LIMIT_REACHED");
        }

        if (Network.getByName(session, name) != null) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "NETWORK_NAME");
        }

        final Network network = Network.createNetwork(session, name, device, isPublic);
        NetworkMember.createMember(session, network, device);
        return new ApiResponse(ApiResponseType.OK, network);
    }

    @ApiEndpoint("members")
    public ApiResponse members(@ApiParameter("network_id") final UUID networkId,
                               @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session
    ) {
        final Network network = Network.getById(session, networkId);

        if (network == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "NETWORK");
        }

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfNetwork(session, network));
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

        return new ApiResponse(ApiResponseType.OK, NetworkMember.getMembershipsOfDevice(session, device));
    }
}
