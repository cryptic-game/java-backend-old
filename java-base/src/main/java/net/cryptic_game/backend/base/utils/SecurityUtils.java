package net.cryptic_game.backend.base.utils;

import com.google.gson.JsonParseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;

public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException();
    }

    public static String jwt(final Key key, final Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static Claims parseJwt(final Key key, final String jwt) throws JsonParseException, SignatureException, ExpiredJwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}
