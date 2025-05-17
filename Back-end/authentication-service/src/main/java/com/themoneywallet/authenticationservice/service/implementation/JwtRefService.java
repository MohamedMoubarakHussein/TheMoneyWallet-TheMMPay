package com.themoneywallet.authenticationservice.service.implementation;


import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
     @Override
    public boolean isTokenValid(String token) {
        try {return handleClamis(token);}
         catch (Exception e) {return false;}
        }
        @Override
        public boolean handleClamis(String token){
       Claims claim = this.extractInfoFromToken(token);
       boolean fact1 =  claim.getExpiration().after(new Date());
       // add more than one fact if you want
       return fact1;
    }
    

    public Optional<String> extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        
        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }
        
        return Optional.empty();
    }
    
    public void addNewAccessTokenCookie(HttpServletResponse response, String newAccessToken) {
        Cookie accessTokenCookie = new Cookie("access_token", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true); // For HTTPS
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600); // 1 hour expiry
        response.addCookie(accessTokenCookie);
    }

}
