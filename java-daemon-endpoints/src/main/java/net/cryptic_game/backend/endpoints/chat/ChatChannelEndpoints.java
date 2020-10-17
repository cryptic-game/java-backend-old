package net.cryptic_game.backend.endpoints.chat;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.DaemonAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.utils.DaemonUtils;
import net.cryptic_game.backend.data.sql.entities.chat.ChatAction;
import net.cryptic_game.backend.data.sql.entities.chat.ChatChannel;
import net.cryptic_game.backend.data.sql.entities.chat.ChatChannelAccess;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatMessageRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ApiEndpointCollection(id = "chat/channel", description = "join/leave/create/rename a channel. Get information/members", type = ApiType.REST, authenticator = DaemonAuthenticator.class)
public final class ChatChannelEndpoints {

    private final ChatChannelRepository channelRepository;
    private final ChatChannelAccessRepository channelAccessRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    @ApiEndpoint(id = "create")
    public ApiResponse create(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "name") final String channelName) {
        if (channelName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final ChatChannel newChannel = this.channelRepository.create(channelName);
        this.channelAccessRepository.create(this.userRepository.findById(userId).orElse(null), newChannel);
        return new ApiResponse(HttpResponseStatus.OK, newChannel);
    }

    @ApiEndpoint(id = "rename")
    public ApiResponse rename(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "channel_id") final UUID channelId,
                              @ApiParameter(id = "new_name") final String newName) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (newName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "ACCESS_DENIED");
        }

        channel.setName(newName);
        this.channelRepository.save(channel);

        members.parallelStream()
                .filter(member -> !member.equals(user))
                .forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.CHANNEL_RENAMED, channel));
        return new ApiResponse(HttpResponseStatus.OK, channel);
    }

    @ApiEndpoint(id = "info")
    public ApiResponse info(@ApiParameter(id = "channel_id") final UUID channelId) {
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        return new ApiResponse(HttpResponseStatus.OK, channel);
    }

    @ApiEndpoint(id = "members")
    public ApiResponse getMembers(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                                  @ApiParameter(id = "channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(HttpResponseStatus.OK, members);
    }

    @ApiEndpoint(id = "join")
    public ApiResponse join(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (members.contains(user)) {
            return new ApiResponse(HttpResponseStatus.CONFLICT, "ALREADY_IN_CHANNEL");
        }

        final JsonObject userJson = user.serializePublic();
        members.parallelStream().forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_JOIN, userJson));

        return new ApiResponse(HttpResponseStatus.OK, this.channelAccessRepository.create(user, channel));
    }

    @ApiEndpoint(id = "leave")
    public ApiResponse leave(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                             @ApiParameter(id = "channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElseThrow();
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        ChatChannelAccess channelAccess = this.channelAccessRepository.findByUserAndChannel(user, channel).orElse(null);

        if (channelAccess == null) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "NOT_IN_CHANNEL");
        }

        this.channelAccessRepository.delete(channelAccess);

        final List<User> members = this.channelAccessRepository.getMembers(channel);
        members.parallelStream().forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_LEAVE, user.serializePublic()));

        if (members.size() == 0) {
            this.channelAccessRepository.deleteAllByChannel(channel);
            this.messageRepository.deleteAllByChannel(channel);
            this.channelRepository.delete(channel);
        }

        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "list")
    public ApiResponse list(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "public", required = false) final boolean isPublic) {
        if (isPublic) {
            return new ApiResponse(HttpResponseStatus.OK, this.channelRepository.findAll());
        } else {
            User user = this.userRepository.findById(userId).orElse(null);
            return new ApiResponse(HttpResponseStatus.OK, this.channelAccessRepository.getChannels(user));
        }
    }
}
