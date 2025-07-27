package com.themoneywallet.usermanagmentservice.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    
}