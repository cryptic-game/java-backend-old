package net.cryptic_game.backend.endpoints.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.data.chat.ChatAction;
import net.cryptic_game.backend.data.chat.ChatChannel;
import net.cryptic_game.backend.data.chat.ChatChannelAccess;
import net.cryptic_game.backend.data.user.User;

import java.util.List;
import java.util.UUID;

public final class ChatChannelEndpoints extends ApiEndpointCollection {

    public ChatChannelEndpoints() {
        super("chat/channel", "join/leave/create/rename a channel. Get information/members");
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "name") final String channelName) {
        if (channelName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final ChatChannel newChannel = ChatChannel.createChannel(channelName);
        ChatChannelAccess.create(User.getById(userId), newChannel);
        return new ApiResponse(ApiResponseType.OK, newChannel);
    }

    @ApiEndpoint("rename")
    public ApiResponse rename(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("channel_id") final UUID channelId,
                              @ApiParameter("new_name") final String newName) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (newName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        channel.setName(newName);
        channel.saveOrUpdate();

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), ChatAction.CHANNEL_RENAMED, channel);
        }
        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter("channel_id") final UUID channelId) {
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("members")
    public ApiResponse getMembers(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                  @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, members);
    }

    @ApiEndpoint("join")
    public ApiResponse join(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = ChatChannelAccess.getMembers(channel);

        if (members.contains(user)) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_IN_CHANNEL");
        }

        final JsonObject userJson = user.serializePublic();
        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_JOIN, userJson);
        }

        return new ApiResponse(ApiResponseType.OK, ChatChannelAccess.create(user, channel));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        ChatChannelAccess channelAccess = ChatChannelAccess.get(user, channel);

        if (channelAccess == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_IN_CHANNEL");
        }

        channelAccess.delete();

        final List<User> members = ChatChannelAccess.getMembers(channel);

        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_LEAVE, user.serializePublic());
        }

        if (members.size() == 0) {
            channel.delete();
        }

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "public", optional = true) final boolean isPublic) {
        if (isPublic) {
            return new ApiResponse(ApiResponseType.OK, ChatChannel.getChannels());
        } else {
            User user = User.getById(userId);
            return new ApiResponse(ApiResponseType.OK, ChatChannelAccess.getChannels(user));
        }
    }
}
