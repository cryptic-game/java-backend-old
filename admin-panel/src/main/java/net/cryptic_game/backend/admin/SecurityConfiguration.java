package net.cryptic_game.backend.admin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.FormLoginSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.HttpBasicSpec;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import reactor.core.publisher.Mono;

@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private static void setField(final Class<?> clazz, final String field, final Object object, final Object value) {
        try {
            final Field authorities = clazz.getDeclaredField(field);
            authorities.setAccessible(true);
            authorities.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity http) {
        http.csrf(CsrfSpec::disable);
        http.formLogin(FormLoginSpec::disable);
        http.httpBasic(HttpBasicSpec::disable);

        final WebSessionServerRequestCache webSessionServerRequestCache = new WebSessionServerRequestCache();
        http.requestCache(spec -> spec.requestCache(webSessionServerRequestCache));

        http.oauth2Login(spec -> spec.authenticationSuccessHandler((webFilterExchange, authentication) -> {
            final DefaultOAuth2User principal = ((DefaultOAuth2User) authentication.getPrincipal());
            final Collection<String> tokenRoles = principal.getAttribute("roles");

            if (tokenRoles != null) {
                final Set<SimpleGrantedAuthority> roles = tokenRoles.stream()
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

                setField(DefaultOAuth2User.class, "authorities", principal, newPrincipleAuthorities);
                setField(AbstractAuthenticationToken.class, "authorities", authentication, newTokenAuthorities);
            } else {
                log.error("");
                log.error("OIDC: Missing \"roles\" in token/userinfo.");
                log.error("");
            }

            final RedirectServerAuthenticationSuccessHandler handler = new RedirectServerAuthenticationSuccessHandler("/auth/success");
            handler.setRequestCache(webSessionServerRequestCache);
            return handler.onAuthenticationSuccess(webFilterExchange, authentication);
        }));

        http.authorizeExchange()

                .pathMatchers(HttpMethod.GET, "/", "/swagger-ui", "/webjars/**", "/v3/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.HEAD, "/", "/swagger-ui", "/webjars/**", "/v3/api-docs/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS, "/", "/swagger-ui", "/webjars/**", "/v3/api-docs/**").permitAll()

                .pathMatchers(HttpMethod.GET, "/website/**").permitAll()
                .pathMatchers(HttpMethod.HEAD, "/website/**").permitAll()
                .pathMatchers(HttpMethod.OPTIONS, "/website/**").permitAll()

                .pathMatchers("/website/**").hasRole("WEBSITE")

                .pathMatchers("/server_management/**").hasRole("SERVER_ADMIN")

                .pathMatchers(HttpMethod.GET, "/actuator/health").permitAll()

                // SERVER_ADMIN, MODERATOR

//                .pathMatchers("/users/{username}").access((authentication, context) ->
//                authentication
//                        .map(Authentication::getName)
//                        .map((username) -> username.equals(context.getVariables().get("username")))
//                        .map(AuthorizationDecision::new))

                .anyExchange().authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint((exchange, ex) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }
}
