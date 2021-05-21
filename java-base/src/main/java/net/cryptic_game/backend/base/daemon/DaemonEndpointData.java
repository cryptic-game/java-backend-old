package net.cryptic_game.backend.base.daemon;

import java.lang.reflect.Method;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterData;

@Getter
@Setter
@EqualsAndHashCode
public final class DaemonEndpointData extends ApiEndpointData {

    private static final ApiParameterData[] EMPTY_PARAMETERS = new ApiParameterData[0];

    private Daemon daemon;

    public DaemonEndpointData(final String id, final String description, final int authentication,
                              final boolean disabled, final ApiAuthenticator authenticator, final Object instance, final Class<?> clazz,
                              final Method method, final Daemon daemon) {
        super(description, authentication, clazz, disabled, authenticator, id, EMPTY_PARAMETERS, instance, method);
        this.daemon = daemon;
    }
}
