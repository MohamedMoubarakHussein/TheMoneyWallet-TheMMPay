package com.gatewayApi.Service.shared;

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
    public String msg ;
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }


    public boolean isTokenValid(String token) {
   
           try {
             return handleClamis(token);
           } catch (Exception e) {
            msg = e.getMessage();
            return false;
           }
           
   
        }
    public String whyNotValid(){
        return msg;
    }



    public Claims extractInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
    }

    public boolean handleClamis(String token){
       Claims claim = this.extractInfoFromToken(token);
       log.info(token);
       log.info(claim.toString());
       
       boolean fact1 =  claim.getExpiration().after(new Date());
       log.info(fact1+" ");
       // add more than one fact if you want
       return fact1;
    }

    
}
