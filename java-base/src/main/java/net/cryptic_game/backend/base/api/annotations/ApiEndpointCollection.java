package net.cryptic_game.backend.base.api.annotations;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEndpointCollection {

    @NotNull
    String id();

    @NotNull
    String[] description();
}
