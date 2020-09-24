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
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelAccessRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatChannelRepository;
import net.cryptic_game.backend.data.sql.repositories.chat.ChatMessageRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public final class ChatChannelEndpoints extends ApiEndpointCollection {

    private final ChatChannelRepository channelRepository;
    private final ChatChannelAccessRepository channelAccessRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;

    public ChatChannelEndpoints(final ChatChannelRepository channelRepository,
                                final ChatChannelAccessRepository channelAccessRepository,
                                final ChatMessageRepository messageRepository,
                                final UserRepository userRepository) {
        super("chat/channel", "join/leave/create/rename a channel. Get information/members");
        this.channelRepository = channelRepository;
        this.channelAccessRepository = channelAccessRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @ApiEndpoint("create")
    public ApiResponse create(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter(value = "name") final String channelName) {
        if (channelName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final ChatChannel newChannel = this.channelRepository.create(channelName);
        this.channelAccessRepository.create(this.userRepository.findById(userId).orElse(null), newChannel);
        return new ApiResponse(ApiResponseType.OK, newChannel);
    }

    @ApiEndpoint("rename")
    public ApiResponse rename(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("channel_id") final UUID channelId,
                              @ApiParameter("new_name") final String newName) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        if (newName.length() > ChatChannel.MAX_NAME_LENGTH) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NAME_TOO_LONG");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        channel.setName(newName);
        this.channelRepository.save(channel);

        members.parallelStream()
                .filter(member -> !member.equals(user))
                .forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.CHANNEL_RENAMED, channel));
        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("info")
    public ApiResponse info(@ApiParameter("channel_id") final UUID channelId) {
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        return new ApiResponse(ApiResponseType.OK, channel);
    }

    @ApiEndpoint("members")
    public ApiResponse getMembers(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                                  @ApiParameter("channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (!members.contains(user)) {
            return new ApiResponse(ApiResponseType.UNAUTHORIZED, "ACCESS_DENIED");
        }

        return new ApiResponse(ApiResponseType.OK, members);
    }

    @ApiEndpoint("join")
    public ApiResponse join(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        final List<User> members = this.channelAccessRepository.getMembers(channel);

        if (members.contains(user)) {
            return new ApiResponse(ApiResponseType.ALREADY_EXISTS, "ALREADY_IN_CHANNEL");
        }

        final JsonObject userJson = user.serializePublic();
        members.parallelStream().forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_JOIN, userJson));

        return new ApiResponse(ApiResponseType.OK, this.channelAccessRepository.create(user, channel));
    }

    @ApiEndpoint("leave")
    public ApiResponse leave(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                             @ApiParameter("channel_id") final UUID channelId) {
        final User user = this.userRepository.findById(userId).orElse(null);
        final ChatChannel channel = this.channelRepository.findById(channelId).orElse(null);

        if (channel == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "CHANNEL_NOT_FOUND");
        }

        ChatChannelAccess channelAccess = this.channelAccessRepository.findByUserAndChannel(user, channel).orElse(null);

        if (channelAccess == null) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "NOT_IN_CHANNEL");
        }

        this.channelAccessRepository.delete(channelAccess);

        final List<User> members = this.channelAccessRepository.getMembers(channel);
        members.parallelStream().forEach(member -> DaemonUtils.notifyUser(member.getId(), ChatAction.MEMBER_LEAVE, user.serializePublic()));

        if (members.size() == 0) {
            this.channelAccessRepository.deleteAllByChannel(channel);
            this.messageRepository.deleteAllByChannel(channel);
            this.channelRepository.delete(channel);
        }

        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint("list")
    public ApiResponse list(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter(value = "public", optional = true) final boolean isPublic) {
        if (isPublic) {
            return new ApiResponse(ApiResponseType.OK, this.channelRepository.findAll());
        } else {
            User user = this.userRepository.findById(userId).orElse(null);
            return new ApiResponse(ApiResponseType.OK, this.channelAccessRepository.getChannels(user));
        }
    }
}
