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

    private final int maxMessageLength =  256;

    @ApiEndpoint("send")
    public ApiResponse send(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("chat_id") final UUID chatId,
                            @ApiParameter("message") final String message) {
        ChatChannel channel = ChatChannel.getById(chatId);
        User user = User.getById(userId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > maxMessageLength) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), "chat_message", ChatAction.SEND_MESSAGE);
        }

        ChatMessage msg = ChatMessage.create(channel, user, message);
        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("whisper")
    public ApiResponse whisper(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter("chat_id") final UUID chatId,
                               @ApiParameter("message") final String message,
                               @ApiParameter("target") final UUID targetId) {
        User target = User.getById(targetId);
        User self = User.getById(userId);
        ChatChannel channel = ChatChannel.getById(chatId);

        if (target == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_FOUND");
        }

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > maxMessageLength) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(self)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        if (!members.contains(target)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_IN_CHANNEL");
        }

        DaemonUtils.notifyUser(target.getId(), "chat_message", ChatAction.WHISPER_MESSAGE);
        ChatMessage msg = ChatMessage.create(channel, self, target, message);
        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("message_id") final UUID msgId) {
        ChatMessage message = ChatMessage.getById(ChatMessage.class, msgId);
        User user = User.getById(userId);

        if (message == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MESSAGE_NOT_FOUND");
        }

        if (!message.getUser().equals(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "NOT_YOUR_MESSAGE");
        }

        for (User member : ChatChannelAccess.getMembers(message.getChannel())) {
            if (user.equals(member)) continue;
            DaemonUtils.notifyUser(member.getId(), "chat_message", ChatAction.MESSAGE_DELETED);
        }
        message.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse getMessages(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("chat_id") final UUID channelId,
                                   @ApiParameter("begin") final int bSeconds,
                                   @ApiParameter("end") final int eSeconds) {
        ChatChannel channel = ChatChannel.getById(channelId);
        User user = User.getById(userId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        List<User> members = ChatChannelAccess.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "NOT_IN_CHANNEL");
        }

        OffsetDateTime end = OffsetDateTime.now().minusSeconds(eSeconds);
        OffsetDateTime begin = OffsetDateTime.now().minusSeconds(bSeconds);
        List<ChatMessage> messages = ChatMessage.getMessages(channel, user, begin, end);
        return new ApiResponse(ApiResponseType.OK, messages);
    }

    @ApiEndpoint("list_last_hour")
    public ApiResponse getMessages(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("chat_id") final UUID channelId) {
        return getMessages(userId, channelId, 60 * 60, 0);
    }
}
