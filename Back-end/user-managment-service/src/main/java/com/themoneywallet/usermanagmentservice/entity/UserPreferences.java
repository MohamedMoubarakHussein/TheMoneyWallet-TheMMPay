package com.themoneywallet.usermanagmentservice.entity;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_prefernces")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;


    private boolean emailNotificationsEnabled ;
    private String language ;
    private String timezone ;
    
    @ElementCollection
    @Builder.Default
    private Map<String, String> additionalPreferences = new HashMap<>();


}
