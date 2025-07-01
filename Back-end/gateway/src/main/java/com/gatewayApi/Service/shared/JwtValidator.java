package com.gatewayApi.Service.shared;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.springframework.stereotype.Component;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
    private Integer status ;
    private String newToken;

  
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }


    public Map<Integer, Object> isTokenValid(String token) {
   

             return handleClamis(token);
           
           
   
        }
    
  
    public String whyNotValid(){
        return msg;
    }



    public Claims extractInfoFromToken(String token) {
        
        try {
             return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
                log.info("exiptred token   ");
                throw e;
            
        }
       
    }

    public Map<Integer, Object> handleClamis(String token)  {
 
        Claims claim = this.extractInfoFromToken(token );
       
       
       
       
       boolean fact1 =  claim.getExpiration().after(new Date());
      
       Map<Integer , Object> mp =  new HashMap<>();
       mp.put(1, String.valueOf(fact1));
       mp.put(2, status);
       mp.put(3 , this.newToken);
      return mp;
    }

    
}
