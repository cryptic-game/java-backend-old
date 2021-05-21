package net.cryptic_game.backend.endpoints.chat;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.sql.entities.chat.ChatAction;
import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.sql.entities.chat.ChatMessage;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatMessageRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.endpoints.NotificationService;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "chat/message", description = "send/whisper/delete/list messages", type = ApiType.REST)
public final class ChatMessageEndpoint {

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final ChatChannelRepository channelRepository;
    private final ChatChannelAccessRepository channelAccessRepository;
    private final ChatMessageRepository messageRepository;

    @ApiEndpoint(id = "send")
    public ApiResponse send(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "channel_id") final UUID channelId,
                            @ApiParameter(id = "message") final String message) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "ACCESS_DENIED");
        }

        final ChatMessage msg = this.messageRepository.create(user, channel, message, null);

        JsonElement json = JsonBuilder.create("action", ChatAction.SEND_MESSAGE).add("message", msg).build();

        members.stream()
                .filter(member -> !member.equals(user))
                .forEach(member -> this.notificationService.sendNotification(member.getId(), "chat", json));

        return new ApiResponse(HttpResponseStatus.OK, msg);
    }

    @ApiEndpoint(id = "whisper")
    public ApiResponse whisper(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                               @ApiParameter(id = "channel_id") final UUID channelId,
                               @ApiParameter(id = "message") final String message,
                               @ApiParameter(id = "target") final UUID targetId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);
        final User target = this.userRepository.findById(targetId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (target == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "TARGET_NOT_FOUND");
        }

        if (message.length() > ChatMessage.MAX_MESSAGE_LENGTH) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "MESSAGE_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "ACCESS_DENIED");
        }

        if (!members.contains(target)) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "TARGET_NOT_IN_CHANNEL");
        }

        final ChatMessage msg = this.messageRepository.create(user, channel, message, target);
        this.notificationService.sendNotification(target.getId(), "chat", JsonBuilder.create("action", ChatAction.WHISPER_MESSAGE).add("message", msg).build());
        return new ApiResponse(HttpResponseStatus.OK, msg);
    }

    @ApiEndpoint(id = "delete")
    public ApiResponse delete(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "message_id") final UUID msgId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatMessage message = this.messageRepository.findById(msgId).orElse(null);

        if (message == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "MESSAGE_NOT_FOUND");
        }

        if (!message.getUser().equals(user)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "NOT_YOUR_MESSAGE");
        }

        JsonElement json = JsonBuilder.create("action", ChatAction.MESSAGE_DELETED).add("message_id", message.getId()).build();
        if (message.getTarget() == null) {
            this.channelAccessRepository.getMembers(message.getChannel()).stream()
                    .filter(member -> !member.equals(user))
                    .forEach(member -> this.notificationService.sendNotification(member.getId(), "chat", json));
        } else {
            this.notificationService.sendNotification(message.getTarget().getId(), "chat", json);
        }

        this.messageRepository.delete(message);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "list")
    public ApiResponse getMessages(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                                   @ApiParameter(id = "chat_id") final UUID channelId,
                                   @ApiParameter(id = "page") final int page,
                                   @ApiParameter(id = "page_size") final int pageSize) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (this.channelAccessRepository.findByUserAndChannel(user, channel).isEmpty()) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "ACCESS_DENIED");
        }
        return new ApiResponse(HttpResponseStatus.OK, messageRepository.getMessages(channel, user, PageRequest.of(page, pageSize)));
    }
}
