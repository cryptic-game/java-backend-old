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
import net.cryptic_game.backend.data.sql.entities.chat.ChatMessage;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatMessageRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public final class ChatMessageEndpoint extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final ChatChannelRepository channelRepository;
    private final ChatChannelAccessRepository channelAccessRepository;
    private final ChatMessageRepository messageRepository;

    public ChatMessageEndpoint(final UserRepository userRepository,
                               final ChatChannelRepository channelRepository,
                               final ChatChannelAccessRepository channelAccessRepository,
                               final ChatMessageRepository messageRepository) {
        super("chat/message", "send/whisper/delete/list messages");
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.channelAccessRepository = channelAccessRepository;
        this.messageRepository = messageRepository;
    }

    @ApiEndpoint("send")
    public ApiResponse send(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("channel_id") final UUID channelId,
                            @ApiParameter("message") final String message) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        final ChatMessage msg = this.messageRepository.create(user, channel, message, null);

        members.parallelStream()
                .filter(member -> !member.equals(user))
                .forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.SEND_MESSAGE, msg));

        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("whisper")
    public ApiResponse whisper(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                               @ApiParameter("channel_id") final UUID channelId,
                               @ApiParameter("message") final String message,
                               @ApiParameter("target") final UUID targetId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);
        final User target = this.userRepository.findById(targetId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (target == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        if (!members.contains(target)) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "TARGET_NOT_IN_CHANNEL");
        }

        final ChatMessage msg = this.messageRepository.create(user, channel, message, target);
        DaemonUtils.notifyUser(target.getId(), ChatAction.WHISPER_MESSAGE, msg);
        return new ApiResponse(ApiResponseType.OK, msg);
    }

    @ApiEndpoint("delete")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("message_id") final UUID msgId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatMessage message = this.messageRepository.findById(msgId).orElse(null);

        if (message == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "MESSAGE_NOT_FOUND");
        }

        if (!message.getUser().equals(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "NOT_YOUR_MESSAGE");
        }

        if (message.getTarget() == null) {
            this.channelAccessRepository.getMembers(message.getChannel()).parallelStream()
                    .filter(member -> !member.equals(user))
                    .forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.MESSAGE_DELETED, message));
        } else {
            DaemonUtils.notifyUser(message.getTarget().getId(), ChatAction.MESSAGE_DELETED, message);
        }

        this.messageRepository.delete(message);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse getMessages(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                   @ApiParameter("chat_id") final UUID channelId,
                                   @ApiParameter("page") final int page,
                                   @ApiParameter("page_size") final int pageSize) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (this.channelAccessRepository.findByUserAndChannel(user, channel).isEmpty()) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }
        return new ApiResponse(ApiResponseType.OK, messageRepository.getMessages(channel, user, PageRequest.of(page, pageSize)));
    }
}
