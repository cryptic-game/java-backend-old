package net.cryptic_game.backend.endpoints.chat;

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

    private final int maxNameLength = 32;

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "name") final String channelName) {

        if (channelName.length() > this.maxNameLength) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        ChatChannel newChannel = ChatChannel.createChannel(channelName);
        ChatChannelAccess.create(User.getById(userId), newChannel);
        return new ApiResponse(ApiResponseType.OK, newChannel);
    }

    @ApiEndpoint("rename")
    public ApiResponse rename(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("chat_id") final UUID channelId,
                              @ApiParameter("new_name") final String newName) {
        ChatChannel channel = ChatChannel.getById(channelId);
        User user = User.getById(userId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (newName.length() > this.maxNameLength) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), "chat_channel", ChatAction.CHANNEL_RENAMED);
        }

        channel.setName(newName);
        channel.saveOrUpdate();
        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("chat_id") final UUID channelId) {
        ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("members")
    public ApiResponse getMembers(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                  @ApiParameter("chat_id") final UUID channelId) {
        ChatChannel channel = ChatChannel.getById(channelId);
        User user = User.getById(userId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, members);
    }

    @ApiEndpoint("join")
    public ApiResponse join(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("chat_id") final UUID chatId) {
        User user = User.getById(userId);
        ChatChannel channel = ChatChannel.getById(chatId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (members.contains(user)) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_IN_CHANNEL");
        }

        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), "chat_channel", ChatAction.MEMBER_JOIN);
        }

        ChatChannelAccess chatChannelAccess = ChatChannelAccess.create(user, channel);
        return new ApiResponse(ApiResponseType.OK, chatChannelAccess);
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter("chat_id") final UUID chatId) {
        User user = User.getById(userId);
        ChatChannel channel = ChatChannel.getById(chatId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_IN_CHANNEL");
        }

        ChatChannelAccess.get(user, channel).delete();
        members = ChatChannelAccess.getMembers(channel);

        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), "chat_channel", ChatAction.MEMBER_LEAVE);
        }

        if (members.size() == 0) {
            channel.delete();
        }

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("channels")
    public ApiResponse getChannels(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId) {
        User user = User.getById(userId);
        return new ApiResponse(ApiResponseType.OK, ChatChannelAccess.getChannels(user));
    }
}
