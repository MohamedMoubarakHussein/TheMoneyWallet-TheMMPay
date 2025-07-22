package com.themoneywallet.authenticationservice.service.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import com.themoneywallet.authenticationservice.service.defintion.JwtServiceDefintion;
import com.themoneywallet.sharedUtilities.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConfigurationProperties(prefix =  "app.jwt")
public class JwtService implements JwtServiceDefintion {
    private  long JWT_TOKEN_EXPIRATION ; 
    private  String JWT_SECRET;
    
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public String generateToken(String username, UUID userId , String role ) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userid", userId);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {return handleClamis(token);}
        catch (Exception e) {return false;}
    }

    public Claims extractInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey())
               .build()
               .parseClaimsJws(token).getBody();
    }

    public boolean handleClamis(String token){
       Claims claim = this.extractInfoFromToken(token);
       boolean fact1 =  claim.getExpiration().after(new Date());
       // add more than one fact if you want
       return fact1;
    }

    public String extractUserName(String token) {
       return extractInfoFromToken(token).getSubject();
    }


    public UUID extractUserId(String token) {
       return extractInfoFromToken(token).get("userid", UUID.class);
    }

    public UserRole extractUserRole(String token) {
       return extractInfoFromToken(token).get("role", UserRole.class);
    }

}
