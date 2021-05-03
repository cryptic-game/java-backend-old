package net.cryptic_game.backend.admin;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        http.csrf().disable();
        http.oauth2Login();

        http.authorizeExchange()

                .pathMatchers(HttpMethod.GET, "/swagger-ui", "/webjars/**", "/v3/api-docs/**").permitAll()

                .pathMatchers(HttpMethod.GET, "/website/**").permitAll()
                .pathMatchers("/website/**").hasRole("WEBSITE")

//                .pathMatchers("/users/{username}").access((authentication, context) ->
//                authentication
//                        .map(Authentication::getName)
//                        .map((username) -> username.equals(context.getVariables().get("username")))
//                        .map(AuthorizationDecision::new))
                .anyExchange().authenticated();

        return http.build();
    }
}
