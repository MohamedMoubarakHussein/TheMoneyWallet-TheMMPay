package com.themoneywallet.sharedUtilities.utilities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix =  "app.jwt")
public class JwtValidator {
   
    private String secretAccess ;
       

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(secretAccess);
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
            return  (String)claims.get("id");
        }
        return null;
    }

    public String getUserrole(String token) {
        if(this.isTokenValid(token)){
            Claims claims = this.extractInfoFromToken(token);
            return  (String)claims.get("role");
        }
        return null;
    }

    public String getUserName(String token) {
        if(this.isTokenValid(token)){
            Claims claims = this.extractInfoFromToken(token);
            return  (String)claims.get("username");
        }
        return null;
    }
}
