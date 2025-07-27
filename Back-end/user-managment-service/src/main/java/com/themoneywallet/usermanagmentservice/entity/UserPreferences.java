package com.themoneywallet.usermanagmentservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    


}
