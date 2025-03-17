package com.themoneywallet.authenticationservice.config.Security;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.themoneywallet.authenticationservice.service.implementation.JwtService;
import com.themoneywallet.authenticationservice.service.implementation.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final MyUserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                String header = request.getHeader("Authorization");

                if(StringUtils.isEmpty(header) || !StringUtils.startsWith(header, "Bearer ")){
                    filterChain.doFilter(request, response);
                    return;
                }

                String token = header.substring(7);
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    UserDetails user = this.userDetailsService.loadUserByUsername(this.jwtService.extractUserName(token));
                    if(this.jwtService.isTokenValid(token)){
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null , user.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(auth);
                        SecurityContextHolder.setContext(context);

                    }
                }
                filterChain.doFilter(request, response);
        }


}
