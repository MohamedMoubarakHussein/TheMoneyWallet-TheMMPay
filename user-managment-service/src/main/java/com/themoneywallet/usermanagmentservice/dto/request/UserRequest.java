package com.themoneywallet.usermanagmentservice.dto.request;


import lombok.Getter;

@Getter
public class UserRequest {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
