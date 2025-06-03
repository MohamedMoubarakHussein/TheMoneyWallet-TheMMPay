package com.themoneywallet.authenticationservice.entity;

import com.themoneywallet.authenticationservice.entity.fixed.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "credential")
public class UserCredential implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Column(unique = true, nullable = false)
    @Email(message = "Please provide a valid email")
    private String email;

    @NotNull(message = "user name cannot be null.")
    @Size(
        min = 4,
        max = 16,
        message = "user name should be between 4 and 16 characters."
    )
    private String userName;

    @Size(
        min = 8,
        message = "Password should be between 8 and 32 characters long."
    )
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean locked;
    private boolean enabled;

    private LocalDateTime lastLogin;

    private String emailVerificationToken;

    private String passwordResetToken;

    @Column(unique = true)
    private String token;

    private LocalDateTime validTill;
    private String ipAddress;
    private boolean revoked;

    @PrePersist
    private void intial() {
        this.locked = false;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> auth = new HashSet<>();
        auth.add(new SimpleGrantedAuthority(this.userRole.toString()));
        return auth;
    }

    @Override
    public String getUsername() {
        return this.email;
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
        return validTill.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
