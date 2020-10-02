package net.cryptic_game.backend.base.api.annotations;

import net.cryptic_game.backend.base.api.data.ApiParameterType;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ApiParameter {

    @NotNull
    String id();

    boolean required() default true;

    @NotNull
    ApiParameterType type() default ApiParameterType.NORMAL;

    @NotNull
    String[] description() default "";
}
