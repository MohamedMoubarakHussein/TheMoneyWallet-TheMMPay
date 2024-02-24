package com.themoneywallet.authenticationservice.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";

    public void validateToken(String token){
        Jwts.parserBuilder().setSigningKey(getKey())
            .build()
            .parseClaimsJws(token);
    }
    
    public String generateToken(String email){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }
    private String createToken(Map<String, Object> claims , String email){
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(email)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60))
                   .signWith(getKey(), SignatureAlgorithm.HS256)
                   .compact();
    }
    private Key  getKey(){
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }
}
