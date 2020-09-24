package net.cryptic_game.backend.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.data.sql.entities.user.UserSetting;
import net.cryptic_game.backend.data.sql.repositories.user.UserRepository;
import net.cryptic_game.backend.data.sql.repositories.user.UserSettingRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class UserSettingsEndpoints extends ApiEndpointCollection {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;

    public UserSettingsEndpoints(final UserRepository userRepository,
                                 final UserSettingRepository userSettingRepository) {
        super("settings", "Save, update or delete user settings");
        this.userRepository = userRepository;
        this.userSettingRepository = userSettingRepository;
    }

    @ApiEndpoint(value = "save", description = "Save or override a user setting")
    public ApiResponse save(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("key") final String key,
                            @ApiParameter("value") final String value) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        if (value.length() > 2048) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_VALUE");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            setting = new UserSetting();
            setting.setKey(new UserSetting.UserSettingKey(user, key));
        }
        setting.setValue(value);
        this.userSettingRepository.save(setting);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint(value = "get", description = "Get a user setting")
    public ApiResponse get(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                           @ApiParameter("key") final String key) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SETTING");
        }
        return new ApiResponse(ApiResponseType.OK, setting);
    }

    @ApiEndpoint(value = "delete", description = "Delete a user setting")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("key") final String key) {
        User user = this.userRepository.findById(userId).orElse(null);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = this.userSettingRepository.findByKeyUserAndKeyKey(user, key).orElse(null);
        if (setting == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SETTING");
        }
        this.userSettingRepository.delete(setting);
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint(value = "all", description = "Get all user settings")
    public ApiResponse all(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId) {
        User user = this.userRepository.findById(userId).orElse(null);
        return new ApiResponse(ApiResponseType.OK, this.userSettingRepository.findAllByKeyUser(user));
    }
}
