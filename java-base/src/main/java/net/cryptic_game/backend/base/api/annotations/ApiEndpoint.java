package net.cryptic_game.backend.base.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiEndpoint {

    String id();

    String[] description() default {};

    int authentication() default 0;

    boolean disabled() default false;
}
