package net.cryptic_game.backend.server.authentication;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.Bootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
@Slf4j
public class JwtConfiguration {

    /**
     * Creates a {@link Key} from the secret key given through the environment variables.
     * If the key is to short, the bootstrapping is being canceled.
     *
     * @param bootstrap The {@link Bootstrap}
     * @param config    The {@link JwtConfig} with the raw secret key
     * @return The created {@link Key}
     */
    @Bean
    Key getSigningKey(final Bootstrap bootstrap, final JwtConfig config) {
        final byte[] bytes = config.getKey().getBytes();
        if (bytes.length * 8 < SignatureAlgorithm.HS512.getMinKeyLength()) {
            log.error("A key length of {} bits is too weak! Minimum required is {} bits.", bytes.length * 8, SignatureAlgorithm.HS512.getMinKeyLength());
            bootstrap.shutdown();
        }
        return Keys.hmacShaKeyFor(bytes);
    }
}
