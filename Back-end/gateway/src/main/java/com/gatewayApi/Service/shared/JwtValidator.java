package com.gatewayApi.Service.shared;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.gatewayApi.config.JwtConfig;
import com.themoneywallet.sharedUtilities.utilities.defination.TokenValidator;

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
public class JwtValidator implements TokenValidator {

    private final JwtConfig jwtConfig;
    public String msg;
    private String newToken;

    private Key getKey() {
        byte[] key = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(key);
    }


    @Override
    public boolean isTokenValid(String token) {
        return handleClamisSimple(token);
    }

    private boolean handleClamisSimple(String token){
        try{
            Claims claims = extractInfoFromToken(token);
            return claims.getExpiration().after(new Date());
        }catch(Exception e){
            return false;
        }
    }

   
    public Map<Integer, Object> isTokenValidVerbose(String token) {
        return handleClamisVerbose(token);
    }
    
  
    public String whyNotValid(){
        return msg;
    }



    public Claims extractInfoFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Token expired");
            this.msg = "Token expired";
            throw e;
        } catch (Exception e) {
            log.error("Error parsing token", e);
            this.msg = "Invalid token";
            throw e;
        }
    }

    public Map<Integer, Object> handleClamisVerbose(String token) {
        try {
            Claims claim = this.extractInfoFromToken(token);
            boolean isValid = claim.getExpiration().after(new Date());
            
            Map<Integer, Object> result = new HashMap<>();
            result.put(1, String.valueOf(isValid));
            result.put(2, isValid ? 200 : 401);
            result.put(3, this.newToken);
            return result;
        } catch (Exception e) {
            Map<Integer, Object> result = new HashMap<>();
            result.put(1, "false");
            result.put(2, 401);
            result.put(3, null);
            return result;
        }
    }

    
    @Override
    public java.util.UUID getUserId(String token) {
        try{
            Claims c = extractInfoFromToken(token);
            Object idObj = c.get("id");
            if(idObj instanceof String str){
                return java.util.UUID.fromString(str);
            }
        }catch(Exception e){
            // ignore
        }
        return null;
    }
}
