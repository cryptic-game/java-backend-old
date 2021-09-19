package net.cryptic_game.backend.base.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityUtilsTest {

    @Test
    void testParseJwt() {
        final String subject = "Hallo";
        final String rawKey = "EDjFeyJmDL9zU22zEkx*7XZVmp%UGp0jRR+eLqZDRZCMSFCrrKK4WT4uPVyJJTbAfkT3:wxU~tRUoTgzFjBHKqTh~yjms1obbQmKFgd%6moLEWqZ,7+*+hvM2i:eaTt7+_2V~yR1bXIY9BXAVp_Rb%IqKt0YqkKp_yBYVVYNZ0UT~ayFR9g*6LkA6Io5aBCVRxW.~t,syv4csq_.VgegM7P1S329i.jsqiFC.A1tGdr,**xFx8dRIdA1N0_VGITqtQxn4H:+Dr*%:6+3h2II5yR8yVjYfMhqB,05F0Wex0r+xN9U6TDv6ig+YPZdu4kz2RvSLmseo%.3%dRzLUxTrAyU5n68kjv72sr5bPgh6,C*eg,LHvb0C.TcsMYFBcrITr:sWw5wh1pmuqRGPXky.0pBe8MSWtq8nbrqhG+1Q5+wTJ8WfDw7I%~ixHf2cY4DTyy+kwo~jGxe+umeMyMVCItqj0f9k:QdrPLr9aLBRLzHm4Lk.L3yXpejI8cxy9h9";

        final byte[] keyBytes = rawKey.getBytes(StandardCharsets.UTF_8);
        final Key key = Keys.hmacShaKeyFor(keyBytes);

        final DefaultClaims claims = new DefaultClaims();
        claims.setSubject(subject);

        final String jwt = Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        assertEquals(subject, SecurityUtils.parseJwt(key, jwt).getSubject());
    }
}
