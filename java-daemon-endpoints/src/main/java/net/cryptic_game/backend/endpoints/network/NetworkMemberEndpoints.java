package net.cryptic_game.backend.endpoints.network;

import javassist.compiler.ast.Member;
import net.cryptic_game.backend.base.api.endpoint.*;
import net.cryptic_game.backend.data.device.Device;
import net.cryptic_game.backend.data.network.Network;
import net.cryptic_game.backend.data.network.NetworkMember;
import net.cryptic_game.backend.data.user.User;

import java.util.List;
import java.util.UUID;



public class NetworkMemberEndpoints extends ApiEndpointCollection {

    public NetworkMemberEndpoints() {
        super("network_member");
    }

    @ApiEndpoint("member")
    public ApiResponse member(@ApiParameter("user_id") final UUID user_id,
                              @ApiParameter("device") final UUID device_id) {
        User user = User.getById(user_id);
        Device device = Device.getById(device_id);
        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        List<NetworkMember> membership = NetworkMember.getMembershipsOfDevice(device);

        return new ApiResponse(ApiResponseType.OK, membership);

    }

    @ApiEndpoint("request")
    public ApiResponse request(@ApiParameter("user_id") final UUID user_id,
                               @ApiParameter("device") final UUID device_id,
                               @ApiParameter("id") final UUID id) {
        User user = User.getById(user_id);
        Device device = Device.getById(device_id);
        Network network = Network.getById(id);

        if (!device.hasUserAccess(user))
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_ACCESS_DENIED");

        if (!device.isPoweredOn())
            return new ApiResponse(ApiResponseType.FORBIDDEN, "DEVICE_NOT_ONLINE");

        if (network == null) {
            return  new ApiResponse(ApiResponseType.FORBIDDEN, "NETWORK_NOT_FOUND");
        }
        if(network.getOwner().equals(device)){
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_MEMBER_OF_NETWORK");
        }
        List<NetworkMember> members = (List<NetworkMember>) NetworkMember.getMember(network, device);
        for(NetworkMember member : members) {
            if(member.getDevice().equals(device)){
                return  new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_MEMBER_OF_NETWORK");
            }
        }

        











}
