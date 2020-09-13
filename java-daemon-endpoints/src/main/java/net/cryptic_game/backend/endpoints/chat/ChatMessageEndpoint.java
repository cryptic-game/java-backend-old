package net.cryptic_game.backend.endpoints.chat;

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
import net.cryptic_game.backend.data.sql.entities.chat.ChatMessage;
import net.cryptic_game.backend.data.sql.entities.user.User;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public final class ChatMessageEndpoint extends ApiEndpointCollection {

    public ChatMessageEndpoint() {
        super("chat/message", "send/whisper/delete/list messages");
    }

    @ApiEndpoint("send")
    public ApiResponse send(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                            @ApiParameter("channel_id") final UUID channelId,
                            @ApiParameter("message") final String message) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        final ChatMessage msg = ChatMessage.create(session, channel, user, message);

        for (User member : members) {
            if (member.equals(user)) continue;
            DaemonUtils.notifyUser(member.getId(), ChatAction.SEND_MESSAGE, msg);
        }

        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("whisper")
    public ApiResponse whisper(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                               @ApiParameter("channel_id") final UUID channelId,
                               @ApiParameter("message") final String message,
                               @ApiParameter("target") final UUID targetId) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);
        final User target = User.getById(session, targetId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (target == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = ChatChannelAccess.getMembers(session, channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        if (!members.contains(target)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_IN_CHANNEL");
        }

        final ChatMessage msg = ChatMessage.create(session, channel, user, target, message);
        DaemonUtils.notifyUser(target.getId(), ChatAction.WHISPER_MESSAGE, msg);
        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION_TRANSACTIONAL) final Session session,
                              @ApiParameter("message_id") final UUID msgId) {
        final User user = User.getById(session, userId);
        final ChatMessage message = ChatMessage.getById(session, msgId);

        if (message == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MESSAGE_NOT_FOUND");
        }

        if (!message.getUser().equals(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "NOT_YOUR_MESSAGE");
        }

        if (message.getTarget() == null) {
            for (User member : ChatChannelAccess.getMembers(session, message.getChannel())) {
                if (user.equals(member)) continue;
                DaemonUtils.notifyUser(member.getId(), ChatAction.MESSAGE_DELETED, message);
            }
        } else {
            DaemonUtils.notifyUser(message.getTarget().getId(), ChatAction.MESSAGE_DELETED, message);
        }

        message.delete(session);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse getMessages(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter(value = "session", special = ApiParameterSpecialType.SQL_SESSION) final Session session,
                                   @ApiParameter("chat_id") final UUID channelId,
                                   @ApiParameter("offset") final int offset,
                                   @ApiParameter("count") final int count) {
        final User user = User.getById(session, userId);
        final ChatChannel channel = ChatChannel.getById(session, channelId);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (ChatChannelAccess.get(session, user, channel) == null) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, ChatMessage.getMessages(session, channel, user, offset, count));
    }
}
