package net.cryptic_game.backend.endpoints;

import net.cryptic_game.backend.base.api.endpoint.ApiEndpoint;
import net.cryptic_game.backend.base.api.endpoint.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.endpoint.ApiParameter;
import net.cryptic_game.backend.base.api.endpoint.ApiParameterSpecialType;
import net.cryptic_game.backend.base.api.endpoint.ApiResponse;
import net.cryptic_game.backend.base.api.endpoint.ApiResponseType;
import net.cryptic_game.backend.data.user.User;
import net.cryptic_game.backend.data.user.UserSetting;

import java.util.UUID;

public final class UserSettingsEndpoints extends ApiEndpointCollection {

    public UserSettingsEndpoints() {
        super("settings", "Save, update or delete user settings");
    }

    @ApiEndpoint(value = "save", description = "Save or override a user setting")
    public ApiResponse save(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                            @ApiParameter("key") final String key,
                            @ApiParameter("value") final String value) {
        User user = User.getById(userId);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        if (value.length() > 2048) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_VALUE");
        }

        UserSetting setting = UserSetting.getSetting(user, key);
        if (setting == null) {
            setting = new UserSetting();
            setting.setKey(new UserSetting.UserSettingKey(user, key));
        }
        setting.setValue(value);
        setting.saveOrUpdate();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint(value = "get", description = "Get a user setting")
    public ApiResponse get(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                           @ApiParameter("key") final String key) {
        User user = User.getById(userId);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = UserSetting.getSetting(user, key);
        if (setting == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SETTING");
        }
        return new ApiResponse(ApiResponseType.OK, setting);
    }

    @ApiEndpoint(value = "delete", description = "Delete a user setting")
    public ApiResponse delete(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId,
                              @ApiParameter("key") final String key) {
        User user = User.getById(userId);

        if (key.length() > 256) {
            return new ApiResponse(ApiResponseType.BAD_REQUEST, "INVALID_KEY");
        }

        UserSetting setting = UserSetting.getSetting(user, key);
        if (setting == null) {
            return new ApiResponse(ApiResponseType.NOT_FOUND, "SETTING");
        }
        setting.delete();
        return new ApiResponse(ApiResponseType.OK);
    }

    @ApiEndpoint(value = "all", description = "Get all user settings")
    public ApiResponse all(@ApiParameter(value = "user_id", special = ApiParameterSpecialType.USER) final UUID userId) {
        User user = User.getById(userId);
        return new ApiResponse(ApiResponseType.OK, UserSetting.getSettings(user));
    }
}
