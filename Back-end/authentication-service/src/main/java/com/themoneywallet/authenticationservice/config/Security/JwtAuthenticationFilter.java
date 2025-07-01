package com.themoneywallet.authenticationservice.config.Security;

import com.themoneywallet.authenticationservice.service.implementation.JwtRefService;
import com.themoneywallet.authenticationservice.service.implementation.JwtService;
import com.themoneywallet.authenticationservice.service.implementation.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtRefService jwtRefService;
    private final MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (
            StringUtils.isEmpty(header) ||
            !StringUtils.startsWith(header, "Bearer ")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        try {
            if (
                SecurityContextHolder.getContext().getAuthentication() == null
            ) {
                UserDetails user =
                    this.userDetailsService.loadUserByUsername(
                            this.jwtService.extractUserName(token)
                        );

                if (this.jwtService.isTokenValid(token)) {
                    SecurityContext context =
                        SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                        );
                    auth.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);
                }
            }
        } catch (Exception e) {
            Optional<String> refreshTokenOpt =
                this.jwtRefService.extractRefreshTokenFromCookies(request);

            if (refreshTokenOpt.isPresent()) {
                String refreshToken = refreshTokenOpt.get();
                if (this.jwtRefService.isTokenValid(refreshToken)) {
                    String refreshUsername =
                        this.jwtRefService.extractUserName(refreshToken);

                    String newAccessToken =
                        this.jwtService.generateToken(refreshUsername , null);

                    // Add the new token to the response
                    this.jwtRefService.addNewAccessTokenCookie(
                            response,
                            newAccessToken
                        );
                    SecurityContext context =
                        SecurityContextHolder.createEmptyContext();
                    UserDetails user =
                        this.userDetailsService.loadUserByUsername(
                                refreshUsername
                            );

                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                        );
                    auth.setDetails(
                        new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                    );
                    context.setAuthentication(auth);
                    SecurityContextHolder.setContext(context);

                    response.setHeader(
                        "Authorization",
                        "Bearer " + newAccessToken
                    );
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
