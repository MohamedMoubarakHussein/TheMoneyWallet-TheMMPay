package com.themoneywallet.authenticationservice.service.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.themoneywallet.authenticationservice.service.defintion.JwtServiceDefintion;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService implements JwtServiceDefintion {
    private final MyUserDetailsService userDetailsService;

    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token);
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 10000 * 60 * 60 * 24 * 365))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        String userName = this.extractUserName(token);
        return userName.equals(user.getUsername());

    }

    public boolean isTokenValid(String token) {
       log.info("entring the main mthod");
        try {
            this.extractUserName(token);
            return true;
        } catch (Exception e) {
            log.info("zzzzzzz");
           
        }
        
        return false;

    }

    public String extractUserName(String token) {

        return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}
