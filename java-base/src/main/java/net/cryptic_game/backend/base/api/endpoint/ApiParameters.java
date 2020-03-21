package net.cryptic_game.backend.base.api.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ApiParameters {

    String name();

    ApiParameter[] parameters();

    boolean optional() default false;
}
