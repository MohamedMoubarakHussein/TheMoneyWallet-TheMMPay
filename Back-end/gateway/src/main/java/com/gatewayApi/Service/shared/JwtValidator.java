package com.gatewayApi.Service.shared;

import java.security.Key;
import java.util.Date;

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

  
    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(key);
    }


    public boolean isTokenValid(String token) {
   
           try {
            
             return handleClamis(token);
           }catch (Exception e) {
            msg = e.getMessage();
            return false;
           }
           
   
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
            
            String newToken = this.accToken.getNewAccToken(token);
            return extractInfoFromToken(newToken);
        }
       
    }

    public boolean handleClamis(String token) throws JwtExpiredTokenException {
        log.info(token.trim());
         Claims claim;
            try {
                claim = this.extractInfoFromToken(token);
            } catch (Exception e) {
                claim = null;
                log.info(e.getMessage());
                return false;    
            }
       
       log.info(token);
       log.info(claim.toString());
       
       boolean fact1 =  claim.getExpiration().after(new Date());
      
       log.info(fact1+" ");
       // add more than one fact if you want
       return fact1;
    }

    
}
