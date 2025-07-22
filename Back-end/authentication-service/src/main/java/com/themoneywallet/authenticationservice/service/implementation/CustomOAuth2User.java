package com.themoneywallet.authenticationservice.service.implementation;


import com.themoneywallet.authenticationservice.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final UserCredential userCredential;

    public CustomOAuth2User(OAuth2User oAuth2User, UserCredential userCredential) {
        this.oAuth2User = oAuth2User;
        this.userCredential = userCredential;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userCredential.getAuthorities();
    }

    @Override
    public String getName() {
        return userCredential.getEmail();
    }

    public UserCredential getUserCredential() {
        return userCredential;
    }
}