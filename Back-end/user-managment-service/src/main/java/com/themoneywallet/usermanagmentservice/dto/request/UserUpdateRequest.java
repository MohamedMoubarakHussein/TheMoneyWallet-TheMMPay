package com.themoneywallet.usermanagmentservice.dto.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.themoneywallet.usermanagmentservice.entity.UserPreferences;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails.Address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotNull(message = "first name cannot be null.")
    @Size(min = 4,max = 16,message = "first name should be between 4 and 16 characters.")
    private String firstName;

    @NotNull(message = "last name cannot be null.")
    @Size(min = 4,max = 16,message = "last name should be between 4 and 16 characters.")
    private String lastName;

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
        return "String:firstName;String:lastName;UserPreferences:preferences;LocalDate:dateOfBirth;String:profilePictureUrl;String:bio;Address:address;String:preferredLanguage;String:timezone;";
    }
}
