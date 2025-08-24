package com.themoneywallet.sharedUtilities.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.themoneywallet.sharedUtilities.config.properties.JwtProperties;
import com.themoneywallet.sharedUtilities.utilities.definition.TokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtValidator implements TokenValidator {

    private final JwtProperties jwtProperties;

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(jwtProperties.getSecretAccess());
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


    public UUID getUserId(String token) {
        Claims claims = this.extractInfoFromToken(token);
        return  (UUID)claims.get("id");
    }

    public String getUserrole(String token) {
        Claims claims = this.extractInfoFromToken(token);
        return  (String)claims.get("role");
    }

    public String getUserName(String token) {
        Claims claims = this.extractInfoFromToken(token);
        return  (String)claims.get("username");
    }
}
