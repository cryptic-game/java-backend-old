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
import net.cryptic_game.backend.data.chat.ChatMessage;
import net.cryptic_game.backend.data.user.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class ChatMessageEndpoint extends ApiEndpointCollection {

    public ChatMessageEndpoint() {
        super("chat/message", "send/whisper/delete/list messages");
    }

    @ApiEndpoint("send")
    public ApiResponse send(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("channel_id") final UUID channelId,
                            @ApiParameter("message") final String message) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        final ChatMessage msg = ChatMessage.create(channel, user, message);

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), ChatAction.SEND_MESSAGE, msg);
        }

        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("whisper")
    public ApiResponse whisper(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter("channel_id") final UUID channelId,
                               @ApiParameter("message") final String message,
                               @ApiParameter("target") final UUID targetId) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);
        final User target = User.getById(targetId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (target == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        if (!members.contains(target)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_IN_CHANNEL");
        }

        final ChatMessage msg = ChatMessage.create(channel, user, target, message);
        DaemonUtils.notifyUser(target.getId(), ChatAction.WHISPER_MESSAGE, msg);
        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("message_id") final UUID msgId) {
        final User user = User.getById(userId);
        final ChatMessage message = ChatMessage.getById(msgId);

        if (message == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MESSAGE_NOT_FOUND");
        }

        if (!message.getUser().equals(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "NOT_YOUR_MESSAGE");
        }

        if (message.getTarget() == null) {
            for (User member : ChatChannelAccess.getMembers(message.getChannel())) {
                if (user.equals(member)) continue;
                DaemonUtils.notifyUser(member.getId(), ChatAction.MESSAGE_DELETED, message);
            }
        } else {
            DaemonUtils.notifyUser(message.getTarget().getId(), ChatAction.MESSAGE_DELETED, message);
        }

        message.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse getMessages(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("chat_id") final UUID channelId,
                                   @ApiParameter("begin") final OffsetDateTime begin,
                                   @ApiParameter("end") final OffsetDateTime end) {
        final User user = User.getById(userId);
        final ChatChannel channel = ChatChannel.getById(channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (ChatChannelAccess.get(user, channel) == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, ChatMessage.getMessages(channel, user, begin, end));
    }
}
