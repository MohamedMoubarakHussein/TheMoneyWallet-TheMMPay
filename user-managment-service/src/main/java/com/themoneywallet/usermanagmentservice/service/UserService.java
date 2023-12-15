package com.themoneywallet.usermanagmentservice.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.utilite.GetDataIntegrityErrorMessage;
import com.themoneywallet.usermanagmentservice.utilite.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final GetDataIntegrityErrorMessage getDataIntegrityErrorMessage;
    
    public ResponseEntity<String> signUp(UserRequest userRequest){
        User user = new User();
        this.objectMapper.map(userRequest,user);
        user.setRole(Role.USER);

        try{
            user = this.userRepository.save(user);
        }catch(DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(this.getDataIntegrityErrorMessage.getMessage(e));
        }
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(user.toString());
    }
   
}
