package com.themoneywallet.authenticationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.f4b6a3.uuid.UuidCreator;
import com.themoneywallet.sharedUtilities.enums.UserRole;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "credential")
public class UserCredential implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(unique = true , nullable = false)
    private UUID userId;

    @Column(unique = true, nullable = false , length = 255)
    @Email(message = "Please provide a valid email")
    private String email;

    @Column(unique = true , nullable = false , length = 32)
    @NotNull(message = "user name cannot be null.")
    @Size(min = 4,max = 32,message = "user name should be between 4 and 32 characters.")
    private String userName;

    @Size(min = 8,max = 100,message = "Password should be at least be  8  characters long.")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean locked;
    private boolean enabled;

    private LocalDateTime lastLogin;

    private String emailVerificationToken;
    private LocalDateTime emailTokenValidTill;

    private String passwordResetToken;

    private String token;

    @Transient
    private String accessToken;

    private LocalDateTime tokenValidTill;
    private String ipAddress;
    private boolean revoked;

  
    @Column(name = "oauth2_provider_id")
    private String oauth2ProviderId;

    @Column(name = "oauth2_provider")
    private String oauth2Provider; 

    @Column(name = "is_oauth2_user")
    @Builder.Default
    private boolean isOAuth2User = false;

    @Override
    public String toString(){
        return "String id; String user_id ; string oauth2Provider ; String oauth2ProviderId; boolean isOAuth2User ;String email; userName;  password; userRole; locked; boolean enabled; LocalDateTime lastLogin; String emailVerificationToken; String passwordResetToken;String token; LocalDateTime validTill; String ipAddress; boolean revoked;";
    }
    @PrePersist
    private void intial() {
        this.locked = false;
        this.enabled = true;
        if (this.userId == null) this.userId = UuidCreator.getTimeOrderedEpoch();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> auth = new HashSet<>();
        auth.add(new SimpleGrantedAuthority(this.userRole.toString()));
        return auth;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return tokenValidTill.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
