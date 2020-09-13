package net.cryptic_game.backend.endpoints.chat;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.data.sql.entities.chat.ChatAction;
import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.sql.entities.chat.ChatChannelAccess;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public final class ChatChannelEndpoints extends ApiEndpointCollection {

    public ChatChannelEndpoints() {
        super("chat/channel", "join/leave/create/rename a channel. Get information/members");
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter(value = "name") final String channelName) {
        if (channelName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final ChatChannel newChannel = ChatChannel.createChannel(session, channelName);
        ChatChannelAccess.create(session, User.getById(session, userId), newChannel);
        return new ApiResponse(ApiResponseType.OK, newChannel);
    }

    @ApiEndpoint("rename")
    public ApiResponse rename(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("channel_id") final UUID channelId,
                              @ApiParameter("new_name") final String newName) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (newName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        channel.setName(newName);
        channel.saveOrUpdate(session);

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), ChatAction.CHANNEL_RENAMED, channel);
        }
        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                            @ApiParameter("channel_id") final UUID channelId) {
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("members")
    public ApiResponse getMembers(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                  @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                                  @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, members);
    }

    @ApiEndpoint("join")
    public ApiResponse join(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                            @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        if (members.contains(user)) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_IN_CHANNEL");
        }

        final JsonObject userJson = user.serializePublic();
        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_JOIN, userJson);
        }

        return new ApiResponse(ApiResponseType.OK, ChatChannelAccess.create(session, user, channel));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                             @ApiParameter("channel_id") final UUID channelId) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        ChatChannelAccess channelAccess = ChatChannelAccess.get(session, user, channel);

        if (channelAccess == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_IN_CHANNEL");
        }

        channelAccess.delete(session);

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        for (User member : members) {
            DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_LEAVE, user.serializePublic());
        }

        if (members.size() == 0) {
            channel.delete(session);
        }

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                            @ApiParameter(value = "public", optional = true) final boolean isPublic) {
        if (isPublic) {
            return new ApiResponse(ApiResponseType.OK, ChatChannel.getChannels(session));
        } else {
            User user = User.getById(session, userId);
            return new ApiResponse(ApiResponseType.OK, ChatChannelAccess.getChannels(session, user));
        }
    }
}
