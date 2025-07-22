package com.themoneywallet.authenticationservice.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.authenticationservice.config.Security.JwtAuthenticationFilter;
import com.themoneywallet.authenticationservice.controller.AuthController;
import com.themoneywallet.authenticationservice.dto.request.SignUpRequest;
import com.themoneywallet.authenticationservice.service.implementation.AuthService;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;
import com.themoneywallet.sharedUtilities.utilities.ValidtionRequestHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
/* 
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private ValidtionRequestHandler validation;

    private UnifiedResponse unifiedResponse;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        unifiedResponse = UnifiedResponse.builder()
            .haveData(true)
            .data(
                Map.of(
                    ResponseKey.ERROR.toString(),
                    Map.of(
                        "lastName",
                        "last name cannot be null.",
                        "firstName",
                        "first name cannot be null.",
                        "password",
                        "password cannot be null.",
                        "userName",
                        "user name cannot be null.",
                        "email",
                        "email cannot be null."
                    )
                )
            )
            .haveError(true)
            .timeStamp(null)
            .statusInternalCode("AUVD11001")
            .build();
        when(validation.handle(any(BindingResult.class))).thenReturn(
            unifiedResponse
        );

        when(
            authService.signUp(
                any(SignUpRequest.class),
                any(HttpServletRequest.class)
            )
        ).thenReturn(ResponseEntity.badRequest().body(unifiedResponse));
    }

    @Test
    public void IsArrisingValidationErrors() throws Exception {
        this.mockMvc.perform(
                post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                            {

                            }
                        """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                content()
                    .string(objectMapper.writeValueAsString(unifiedResponse))
            );
    }

    @Test
    public void SignUpFaild() throws Exception {
        this.mockMvc.perform(
                post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                                                 {
                        "userName"  : "asdd",
                         "firstName"  : "adadad",
                          "lastName"  : "adadad",
                          "email"   : "asddd3s@me.com",
                           "password"  : "123456789"
                                                 }
                                             """
                    )
            )
            .andExpect(status().isBadRequest())
            .andExpect(
                content()
                    .string(objectMapper.writeValueAsString(unifiedResponse))
            );
    }
}
*/