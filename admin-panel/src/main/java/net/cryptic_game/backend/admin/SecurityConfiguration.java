package net.cryptic_game.backend.admin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ReactiveClientRegistrationRepository clientRegistrationRepository, final ServerHttpSecurity http) {
        http.csrf().disable();
        final WebSessionServerRequestCache webSessionServerRequestCache = new WebSessionServerRequestCache();
        http.requestCache(spec -> spec.requestCache(webSessionServerRequestCache));

        http.oauth2Login(spec -> {
            spec.authenticationSuccessHandler((webFilterExchange, authentication) -> {

                final DefaultOAuth2User principal = ((DefaultOAuth2User) authentication.getPrincipal());

                final Set<SimpleGrantedAuthority> roles = ((Collection<String>) principal.getAttributes().get("roles"))
                        .stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase(Locale.ROOT)))
                        .collect(Collectors.toSet());

                final Set<GrantedAuthority> newPrincipleAuthorities = Stream.concat(
                        principal.getAuthorities().stream(),
                        roles.stream()
                ).collect(Collectors.toUnmodifiableSet());

                final Set<GrantedAuthority> newTokenAuthorities = Stream.concat(
                        authentication.getAuthorities().stream(),
                        roles.stream()
                ).collect(Collectors.toUnmodifiableSet());

                try {
                    final Field authorities = DefaultOAuth2User.class.getDeclaredField("authorities");
                    authorities.setAccessible(true);
                    authorities.set(principal, newPrincipleAuthorities);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    e.printStackTrace();
                }

                try {
                    final Field authorities = AbstractAuthenticationToken.class.getDeclaredField("authorities");
                    authorities.setAccessible(true);
                    authorities.set(authentication, newTokenAuthorities);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }

                RedirectServerAuthenticationSuccessHandler handler = new RedirectServerAuthenticationSuccessHandler();
                handler.setRequestCache(webSessionServerRequestCache);
                return handler.onAuthenticationSuccess(webFilterExchange, authentication);
            });
        });

        http.authorizeExchange()

                .pathMatchers("/", "/swagger-ui", "/webjars/**", "/v3/api-docs/**").permitAll()

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
