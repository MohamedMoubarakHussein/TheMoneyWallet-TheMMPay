package com.themoneywallet.usermanagmentservice.utilite.shared;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtValidator   {
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";
    
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
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


    
}
