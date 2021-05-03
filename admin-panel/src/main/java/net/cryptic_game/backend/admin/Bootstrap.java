package net.cryptic_game.backend.admin;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Bootstrap {

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
