package net.cryptic_game.backend.admin;

import net.getnova.framework.core.NovaBanner;
import org.apache.commons.lang3.NotImplementedException;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

import java.security.Principal;

@SpringBootApplication
@RestController
@EnableConfigurationProperties(Config.class)
public class Bootstrap {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Bootstrap.class)
                .banner(new NovaBanner())
                .run(args);
    }

    @GetMapping("/user")
    public Principal user(@AuthenticationPrincipal final Principal principal) {
        return principal;
    }

    @GetMapping(value = "/auth", produces = MediaType.TEXT_HTML_VALUE)
    public String auth(@AuthenticationPrincipal final Authentication authentication, final WebSession session) {
        throw new NotImplementedException();
    }

    @Bean
    GroupedOpenApi websiteApi() {
        return GroupedOpenApi.builder()
                .group("cryptic-website")
                .pathsToMatch("/website/**")
                .build();
    }

    @Bean
    GroupedOpenApi serverApi() {
        return GroupedOpenApi.builder()
                .group("cryptic-server")
                .pathsToMatch("/server_management/**")
                .build();
    }
}
