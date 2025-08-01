package com.themoneywallet.notificationservice.utilites.shared;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidator {

    private static final String SECRET =
        "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public boolean isTokenValid(String token) {
        try {
            return handleClamis(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Claims extractInfoFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean handleClamis(String token) {
        Claims claim = this.extractInfoFromToken(token);
        boolean fact1 = claim.getExpiration().after(new Date());
        // add more than one fact if you want
        return fact1;
    }

    public String extractUserName(String token) {
        return extractInfoFromToken(token).getSubject();
    }


    public String getUserId(String token) {
        if(this.isTokenValid(token)){
            Claims claims = this.extractInfoFromToken(token);
            return  (String)claims.get("userid");
        }
        return null;
    }
}
