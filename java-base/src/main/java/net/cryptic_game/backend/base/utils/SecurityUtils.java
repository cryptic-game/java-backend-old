package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.util.Map;

public final class SecurityUtils {

    public static final PasswordEncoder PASSWORD_ENCODER = new DelegatingPasswordEncoder("ARGON2", Map.of(
            "ARGON2", new Argon2PasswordEncoder()
    ));

    private SecurityUtils() {
        throw new UnsupportedOperationException();
    }

    public static String hash(final String content) {
        return PASSWORD_ENCODER.encode(content);
    }

    public static boolean verifyHash(final String content, final String hash) {
        return PASSWORD_ENCODER.matches(content, hash);
    }

    public static String jwt(final Key key, final JsonObject payload) {
        return Jwts.builder().setPayload(payload.toString()).signWith(key, SignatureAlgorithm.HS512).compact();
    }

    public static JsonObject parseJwt(final Key key, final String jwt) throws JsonParseException, SignatureException, ExpiredJwtException {
        return JsonUtils.fromJson(JsonUtils.toJson(Jwts.parserBuilder().setSigningKey(key).build().parse(jwt).getBody()), JsonObject.class);
    }
}
