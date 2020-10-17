package net.cryptic_game.backend.base.api.annotations;

import net.cryptic_game.backend.base.api.data.ApiParameterType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ApiParameter {

    String id();

    boolean required() default true;

    ApiParameterType type() default ApiParameterType.NORMAL;

    String[] description() default {};
}
