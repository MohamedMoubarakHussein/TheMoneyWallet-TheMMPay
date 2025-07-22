package com.themoneywallet.authenticationservice.service.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix =  "app.jwt")
public class JwtRefService   {
    private String REF_SECRET;
    private long REF_TOKEN_EXPIRATION ; 

     private Key getKey() {
        byte[] key = Decoders.BASE64.decode(REF_SECRET);
        return Keys.hmacShaKeyFor(key);
    }

     public String generateToken(String email , UUID userId) {
        return Jwts.builder()
                   .setClaims(new HashMap<>())
                   .setSubject(email)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + REF_TOKEN_EXPIRATION))
                   .signWith(getKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

    
     public Claims extractInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey())
                   .build()
                   .parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token) {
        try {return handleClamis(token);}
        catch (Exception e) {return false;}
    }

    public boolean handleClamis(String token){
        Claims claim = this.extractInfoFromToken(token);
        boolean fact1 =  claim.getExpiration().after(new Date());
        // add more than one fact if you want
        return fact1;   
    }
    
 

}
