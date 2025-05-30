package com.gatewayApi.Service.shared;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.gatewayApi.Service.GetNewAccToken;
import com.gatewayApi.Service.HttpHelper;
import com.gatewayApi.exception.JwtExpiredTokenException;

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

    private final GetNewAccToken accToken;
    private static final String SECRET = "b2phbHpsdU54Z3htb2NSanBCK3ErWkxOeFNmeTdiZk9XNkR2NEt0Mkhraz0=";
    public String msg ;
    private Integer status ;
    private String newToken;

  
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }


    public Map<Integer, Object> isTokenValid(String token , String refCookie) {
   

             return handleClamis(token , refCookie);
           
           
   
        }
    
  
    public String whyNotValid(){
        return msg;
    }



    public Claims extractInfoFromToken(String token  , String refCookie , int second) {
        
        try {
             return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            if(second == 0){
             log.info("Token is exipred trying to get New ref token");
            String newToken = this.accToken.getNewAccToken(refCookie);
            log.info("2Token is exipred trying to get New ref token");

            status = 2;
            this.newToken = newToken;
            return extractInfoFromToken(newToken , refCookie,1);
            }else{
                 return Jwts.parserBuilder().setSigningKey(getKey())
                .build()
                .parseClaimsJws(token).getBody();
            }
            
        }
       
    }

    public Map<Integer, Object> handleClamis(String token , String refCookie)  {
 
        Claims claim = this.extractInfoFromToken(token , refCookie,0);
       
       
       
       
       boolean fact1 =  claim.getExpiration().after(new Date());
      
       Map<Integer , Object> mp =  new HashMap<>();
       mp.put(1, String.valueOf(fact1));
       mp.put(2, status);
       mp.put(3 , this.newToken);
      return mp;
    }

    
}
