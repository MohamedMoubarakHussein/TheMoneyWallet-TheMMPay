package com.themoneywallet.usermanagmentservice.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdate;
import com.themoneywallet.usermanagmentservice.dto.response.UserInformation;
import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.utilite.DatabaseVaildation;
import com.themoneywallet.usermanagmentservice.utilite.ObjectMapper;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final DatabaseVaildation dataVaildation;

    public ResponseEntity<String> signUp(UserRequest userRequest){
        User user = new User();
        this.objectMapper.map(userRequest,user);
        user.setRole(Role.USER);
        if(dataVaildation.isEmailNameExist(user.getEmail())){
            return ResponseEntity.badRequest().body("Email already exists");
 
        }
        if(dataVaildation.isUserNameExist(user.getUserName())){
            return ResponseEntity.badRequest().body("userName already exists");
 
        }

        user = this.userRepository.save(user);
        UserInformation userInformation = new UserInformation();
        this.objectMapper.map(user , userInformation);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(userInformation.toString());
    }

    public ResponseEntity<String> deleteUser(String email) {
        Optional<User> user = this.userRepository.findByEmail(email);
        if(user.isPresent()){
            this.userRepository.delete(user.get());
            return ResponseEntity.status(200).body("User Deleted");
        }
        return ResponseEntity.status(404).body("User Not Found");
    }

    public String getUserByUserName(String userName) {
        Optional<User> user = this.userRepository.findByUserName(userName);
        if(user.isPresent()){
            User usr = user.get();
            return UserInformation.builder().userName(usr.getUserName())
                            .firstName(usr.getFirstName())
                            .lastName(usr.getLastName())
                            .email(usr.getEmail())
                            .role(usr.getRole().toString()).build().toString();
            
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found");
    }

    public String getUserByEmail(String email) {
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent())
            return this.getUserByUserName(usr.get().getUserName());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found");

    }

    @Transactional
    public ResponseEntity<String> updateUser(String email,UserUpdate user) {
       if(!this.dataVaildation.isEmailNameExist(email)){
              return ResponseEntity.status(HttpStatusCode.valueOf(400)).body("This email does not exsist");
       }
       Optional<User> usr = this.userRepository.findByEmail(email);
      // this.deleteUser(email);
       this.objectMapper.map2(user, usr.get());
  
       User usser = this.userRepository.save(usr.get());
       return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(usser.toString());
   }   

    
    public Iterable<User> returnAll(){
        return this.userRepository.findAll();
    }
    
   
}
