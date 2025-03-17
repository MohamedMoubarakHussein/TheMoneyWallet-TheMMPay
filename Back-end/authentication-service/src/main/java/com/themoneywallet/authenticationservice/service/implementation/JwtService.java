package com.themoneywallet.authenticationservice.service.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.themoneywallet.authenticationservice.service.defintion.JwtServiceDefintion;

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
public class JwtService implements JwtServiceDefintion {
    private final long TOKEN_EXPIRATION = 15 * 60 * 1000; // 15 minutes
    private final long REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7 days
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    

    public String generateToken(String email) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
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



     
     public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION)) 
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public String extractUserName(String token) {
       return extractInfoFromToken(token).getSubject();
    }


    
}
