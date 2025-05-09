package com.themoneywallet.authenticationservice.service.implementation;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtRefService extends JwtService {
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdivdZk9XNkR2NEt0Mkhraz0=";
    private final long TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days

    
     private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    @Override
     public String generateToken(String email) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

        @Override
     public Claims extractInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

}
