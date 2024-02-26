package com.themoneywallet.authenticationservice.service.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;


import org.springframework.stereotype.Component;

import com.themoneywallet.authenticationservice.service.defintion.JwtServiceDefintion;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService  implements JwtServiceDefintion{
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";
   
    private Key  getKey(){
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }

    public void validateToken(String token){
        Jwts.parserBuilder().setSigningKey(getKey())
            .build()
            .parseClaimsJws(token);
    }
    
    public String generateToken(String email){
        return Jwts.builder()
                   .setClaims(new HashMap<>())
                   .setSubject(email)
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60))
                   .signWith(getKey(), SignatureAlgorithm.HS256)
                   .compact();
    }

   

    @Override
    public boolean isTokenValid(String token, String email) {
        return Jwts.parserBuilder().setSigningKey(getKey())
        .build()
        .parseClaimsJws(token).getBody().getSubject() == email;
    }
}
