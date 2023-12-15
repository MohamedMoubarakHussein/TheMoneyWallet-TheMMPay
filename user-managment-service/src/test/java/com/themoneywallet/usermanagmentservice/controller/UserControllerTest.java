package com.themoneywallet.usermanagmentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;



@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignUpWithValidData() throws Exception{
        UserRequest userRequest = UserRequest.builder().firstName("!@#!$!@!#$!")
            .lastName("%!#!%!^!").email("testing@testing.testing")
            .password("tngp@#asy!@#(%NDSI%)$WQ@!%$()")
            .userName("testinguserName").build();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(userRequest))
                        )
                        .andExpect(MockMvcResultMatchers.status().isCreated());
        // perform delete for this object
    }

    @Test 
    public void testSignUpWithNoneValidData() throws Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
