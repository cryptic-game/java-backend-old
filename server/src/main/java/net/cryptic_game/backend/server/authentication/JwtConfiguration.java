package net.cryptic_game.backend.server.authentication;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Configuration
@Slf4j
public class JwtConfiguration {

    /**
     * Creates a {@link Key} from the secret key given through the environment variables.
     * If the key is to short, the bootstrapping is being canceled.
     *
     * @param context The {@link ApplicationContext}
     * @param config  The {@link JwtConfig} with the raw secret key
     * @return The created {@link Key}
     */
    @Bean
    Key getSigningKey(final ApplicationContext context, final JwtConfig config) {
        final byte[] bytes = config.getKey().getBytes(StandardCharsets.UTF_8);
        if (bytes.length * 8 < SignatureAlgorithm.HS512.getMinKeyLength()) {
            log.error("A key length of {} bits is too weak! Minimum required is {} bits.", bytes.length * 8, SignatureAlgorithm.HS512.getMinKeyLength());
            SpringApplication.exit(context);
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}
