package com.themoneywallet.usermanagmentservice.entity;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "user_details",indexes = {@Index(columnList = "userName" , unique = true),
                                        @Index(columnList = "email" , unique = true)}
                            ,uniqueConstraints = {@UniqueConstraint(name="uk_email" , columnNames = "email"),
                                                  @UniqueConstraint(name="uk_userName" , columnNames = "userName")})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;



    @NotNull(message = "user name cannot be null.")
    @Size(min=4,max=16,message = "user name should be between 4 and 16 characters.")
    private String userName;
    
    @NotNull(message = "first name cannot be null.")
    @Size(min=4,max=16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @NotNull(message = "last name cannot be null.")
    @Size(min=4,max=16,message = "last name should be between 4 and 16 characters.")
    private String lastName;

    @NotNull(message = "email cannot be null.")
    @Size(min=4,max=64,message = "email should be between 4 and 64 characters.")
    @Email(message = "You should put a vaild email address.")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String password;
    private Date createdAt;

    @PrePersist
    private void preProcessing(){
        this.createdAt = new Date();
    }


}
