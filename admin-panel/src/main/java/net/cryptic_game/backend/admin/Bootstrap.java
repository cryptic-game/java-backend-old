package net.cryptic_game.backend.admin;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@RestController
public class Bootstrap {

    @GetMapping("/user")
    public Principal user(@AuthenticationPrincipal final Principal principal) {
        System.out.println(principal.getClass().getName());
        System.out.println(principal.getName());
        return principal;
    }

    public static void main(final String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }

    @Bean
    GroupedOpenApi cactiApi() {
        return GroupedOpenApi.builder()
                .group("cryptic-website")
                .pathsToMatch("/website/**")
                .build();
    }
}
