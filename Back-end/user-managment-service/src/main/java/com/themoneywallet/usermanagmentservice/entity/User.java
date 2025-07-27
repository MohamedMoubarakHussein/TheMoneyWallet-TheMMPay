package com.themoneywallet.usermanagmentservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.themoneywallet.sharedUtilities.enums.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

    @Id
    private Integer id;

    private String userId;

    @NotNull(message = "user name cannot be null.")
    @Size(min = 4,max = 16,message = "user name should be between 4 and 16 characters.")
    private String userName;

    @NotNull(message = "first name cannot be null.")
    @Size(min = 4,max = 16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @NotNull(message = "last name cannot be null.")
    @Size(min = 4,max = 16,message = "last name should be between 4 and 16 characters.")
    private String lastName;

    @NotNull(message = "email cannot be null.")
    @Size(min = 4,max = 64,message = "email should be between 4 and 64 characters.")
    @Email(message = "You should put a vaild email address.")
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private boolean locked;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SS")
    private LocalDateTime updatedAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonUnwrapped
    private UserPreferences preferences;
    
    private LocalDate dateOfBirth;
    private String profilePictureUrl;
    private String bio;
    private Address address;
    private String preferredLanguage;
    private String timezone;
    @Override
    public String toString() {
        return "Integer:id;String:userId;String:userName;String:firstName;String:lastName;String:email;UserRole:userRole;boolean:locked;LocalDateTime:createdAt;LocalDateTime:updatedAt;UserPreferences:preferences;LocalDate:dateOfBirth;String:profilePictureUrl;String:bio;Address:address;String:preferredLanguage;String:timezone;";
    }
}
