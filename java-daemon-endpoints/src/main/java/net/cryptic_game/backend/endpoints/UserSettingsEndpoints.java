package net.cryptic_game.backend.endpoints;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.DaemonAuthenticator;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserSetting;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSettingRepository;

import java.util.UUID;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "settings", description = "Save, update or delete user settings", type = ApiType.REST, authenticator = DaemonAuthenticator.class)
public final class UserSettingsEndpoints {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    @ApiEndpoint(id = "save", description = "Save or override a user setting")
    public ApiResponse save(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                            @ApiParameter(id = "key") final String key,
                            @ApiParameter(id = "value") final String value) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_KEY");
        }

        if (value.length() > 2048) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_VALUE");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            setting = new UserSetting();
            setting.setKey(new UserSetting.UserSettingKey(user, key));
        }
        setting.setValue(value);
        this.userSettingRepository.save(setting);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "get", description = "Get a user setting")
    public ApiResponse get(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                           @ApiParameter(id = "key") final String key) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "SETTING");
        }
        return new ApiResponse(HttpResponseStatus.OK, setting);
    }

    @ApiEndpoint(id = "delete", description = "Delete a user setting")
    public ApiResponse delete(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId,
                              @ApiParameter(id = "key") final String key) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(HttpResponseStatus.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            return new ApiResponse(HttpResponseStatus.NOT_FOUND, "SETTING");
        }
        this.userSettingRepository.delete(setting);
        return new ApiResponse(HttpResponseStatus.OK);
    }

    @ApiEndpoint(id = "all", description = "Get all user settings")
    public ApiResponse all(@ApiParameter(id = "user_id", type = ApiParameterType.USER) final UUID userId) {
        User user = this.userRepository.findById(userId).orElse(null);
        return new ApiResponse(HttpResponseStatus.OK, this.userSettingRepository.findAllByKeyUser(user));
    }
}
