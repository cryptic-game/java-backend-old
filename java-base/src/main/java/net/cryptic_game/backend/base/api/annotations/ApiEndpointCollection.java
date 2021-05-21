package net.cryptic_game.backend.base.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.cryptic_game.backend.base.api.ApiAuthenticator;
import net.cryptic_game.backend.base.api.DefaultApiAuthenticator;
import net.cryptic_game.backend.base.api.data.ApiType;
import org.springframework.stereotype.Component;

@Component
@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEndpointCollection {

    String id();

    String[] description() default "";

    ApiType type();

    boolean disabled() default false;

    Class<? extends ApiAuthenticator> authenticator() default DefaultApiAuthenticator.class;
}
