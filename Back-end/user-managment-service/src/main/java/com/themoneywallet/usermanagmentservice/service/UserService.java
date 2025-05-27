package com.themoneywallet.usermanagmentservice.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.dto.response.UnifiedResponse;
import com.themoneywallet.usermanagmentservice.dto.response.UserInformation;
import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.utilite.DatabaseVaildation;
import com.themoneywallet.usermanagmentservice.utilite.MyObjectMapper;
import com.themoneywallet.usermanagmentservice.utilite.UnifidResponseHandler;
import com.themoneywallet.usermanagmentservice.utilite.shared.JwtValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final MyObjectMapper objectMapper;
    private final ObjectMapper originalObjectMapper;
    private final DatabaseVaildation dataVaildation;
    private final JwtValidator jwtValidator;
    private final UnifidResponseHandler uResponseHandler;


 
   




    public ResponseEntity<String> signUp(UserRequest userRequest){
        User user = new User();
        this.objectMapper.map(userRequest,user);
        user.setRole(Role.USER);
        if(dataVaildation.isEmailExist(user.getEmail())){
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

    public ResponseEntity<String> deleteUser(String token) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
         if(usr.isPresent()){
            return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , "User deleted"), false, null).toString());
        }   
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not Found"), true, "USR").toString());
     }

    public ResponseEntity<String> getUserByUserName(String userName) {
        Optional<User> usr = this.userRepository.findByUserName(userName);
            
        if(usr.isPresent()){
            User user = usr.get();
            String userInfo = UserInformation.builder().userName(user.getUserName())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .role(user.getRole().toString()).build().toString();
            return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , userInfo), false, null).toString());
        }   
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not Found"), true, "USR").toString());
    }

    public ResponseEntity<String> getUserByEmail(String email) throws JsonProcessingException {
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            String data = this.originalObjectMapper.writeValueAsString(this.uResponseHandler.makResponse(true, Map.of("data" , usr.get().toString()), false, null));
            return ResponseEntity.ok(data);
        }   
        String data = this.originalObjectMapper.writeValueAsString(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not Found"), true, "USR"));

        return ResponseEntity.badRequest().body(data);
    }
    

     public ResponseEntity<String> getIdByToken(String token , String refToken) throws JsonProcessingException {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            String data = this.originalObjectMapper.writeValueAsString(this.uResponseHandler.makResponse(true, Map.of("data" , String.valueOf(usr.get().getId())), false, null));
            return ResponseEntity.ok(data);
        }   
        String data = this.originalObjectMapper.writeValueAsString(this.uResponseHandler.makResponse(true, Map.of("data" , "Token Not Valid"), true, "USR"));
        return ResponseEntity.badRequest().body(data);
    }



    
    @Transactional
    public ResponseEntity<String> updateUser( String token,UserUpdateRequest user) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            this.objectMapper.map2(user, usr.get());
            this.userRepository.save(usr.get());
            return ResponseEntity.ok(this.uResponseHandler.makResponse(false, null, false, null).toString());
        }
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "Token Not Valid"), true, "USR").toString());

    }

     @Transactional
    public ResponseEntity<String> updateUserPrfernce( String token,UserUpdateRequest user) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
           // usr.get().setPreferences(user.getPreferences());
            this.userRepository.save(usr.get());
            return ResponseEntity.ok(this.uResponseHandler.makResponse(false, null, false, null).toString());
        }
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "Token Not Valid"), true, "USR").toString());

    }

     @Transactional
    public ResponseEntity<String> updateUserRole( String token,UserUpdateRequest user) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
           // usr.get().setRole(user.get);
            this.userRepository.save(usr.get());
            return ResponseEntity.ok(this.uResponseHandler.makResponse(false, null, false, null).toString());
        }
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "Token Not Valid"), true, "USR").toString());

    }

    
    public ResponseEntity<String> returnAll(){
       Iterable<User>  users =  this.userRepository.findAll();
       String allUsers =  StreamSupport.stream(users.spliterator(), false) .map(User::toString) .collect(Collectors.joining(", "));
       return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , allUsers), false, null).toString());

    }

      

    
    
  

    public ResponseEntity<String> handleResponse(Map<String,String> data , boolean hData,boolean error , String statusInternal , HttpStatus status ){
         UnifiedResponse unifiedResponse = new UnifiedResponse();
        unifiedResponse.setData(data);
        unifiedResponse.setHaveError(error);
        unifiedResponse.setHaveData(hData);
        unifiedResponse.setStatusInternalCode(statusInternal);
        return new ResponseEntity<>(unifiedResponse.toString(), status);
    }

 



    public ResponseEntity<String> getUserPrefernce(String token) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , usr.get().getPreferences().toString()), false, null).toString());
        }   
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not found"), true, "USR").toString());
    }

    public ResponseEntity<String> getUserProfile(String token) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            User user = usr.get();
            String userInfo = UserInformation.builder().userName(user.getUserName())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getEmail())
                            .role(user.getRole().toString()).build().toString();
            return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , userInfo), false, null).toString());
        }   
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not Found"), true, "USR").toString());
 
    }

    public ResponseEntity<String> getUserRole(String token) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent()){
            return ResponseEntity.ok(this.uResponseHandler.makResponse(true, Map.of("data" , usr.get().getRole().toString()), false, null).toString());
        }   
        return ResponseEntity.badRequest().body(this.uResponseHandler.makResponse(true, Map.of("data" , "User Not found"), true, "USR").toString());
    }
}
