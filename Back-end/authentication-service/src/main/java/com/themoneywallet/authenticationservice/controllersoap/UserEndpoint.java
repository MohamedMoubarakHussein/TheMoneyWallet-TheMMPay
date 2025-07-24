package com.themoneywallet.authenticationservice.controllersoap;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.themoneywallet.authenticationservice.users.GetUserRequest;
import com.themoneywallet.authenticationservice.users.GetUserResponse;
import com.themoneywallet.authenticationservice.users.User;

@Endpoint
public class UserEndpoint {

    private static final String NAMESPACE_URI = "http://example.com/users";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        // Create response object
        GetUserResponse response = new GetUserResponse();
        
        // Create user object
        User user = new User();
        user.setId(request.getId());
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        
        // Set user in response
        response.setUser(user);
        
        return response;
    }
}