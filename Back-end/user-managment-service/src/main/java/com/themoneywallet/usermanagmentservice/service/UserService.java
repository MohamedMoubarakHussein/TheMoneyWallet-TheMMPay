package com.themoneywallet.usermanagmentservice.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.themoneywallet.usermanagmentservice.config.CustomBeans;
import com.themoneywallet.usermanagmentservice.dto.request.UserRequest;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.dto.response.UnifiedResponse;
import com.themoneywallet.usermanagmentservice.dto.response.UserInformation;
import com.themoneywallet.usermanagmentservice.entity.Role;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;
import com.themoneywallet.usermanagmentservice.utilite.DatabaseVaildation;
import com.themoneywallet.usermanagmentservice.utilite.ObjectMapper;
import com.themoneywallet.usermanagmentservice.utilite.shared.JwtValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final DatabaseVaildation dataVaildation;
    private final JwtValidator jwtValidator;


 
   




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

     public String getIdByToken(String token) {
        String email = this.jwtValidator.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if(usr.isPresent())
            return String.valueOf(usr.get().getId());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found");

    }

    @SuppressWarnings("unchecked")
    @Transactional
    public ResponseEntity<String> updateUser(HttpServletRequest request,UserUpdateRequest user) {
        String token = request.getHeader("x-Authorization");
        String email = null;
        if(this.jwtValidator.isTokenValid(token)){
            email =  this.jwtValidator.extractUserName(token);
             if(!this.dataVaildation.isEmailExist(email)){
                   return ResponseEntity.status(HttpStatusCode.valueOf(400)).body( UnifiedResponse.builder().haveError(true)
                              .timeStamp(Instant.now())
                              .haveData(true).data((Map<String, String>) new HashMap<>().put("error", "This email does not exsist")).build().toString() );
             }

             Optional<User> usr = this.userRepository.findByEmail(email);
            this.objectMapper.map2(user, usr.get());
      
  
            this.userRepository.save(usr.get());
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body( UnifiedResponse.builder().haveError(false)
                             .timeStamp(Instant.now())
                            .haveData(false).build().toString() );
        }else{
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body( UnifiedResponse.builder().haveError(true)
                              .timeStamp(Instant.now())
                              .haveData(true).data((Map<String, String>) new HashMap<>().put("error", "Unauthorized")).build().toString() );
        }
        
      
         
      
    }

    
    public Iterable<User> returnAll(){
        return this.userRepository.findAll();
    }

      

    
    
    public ResponseEntity<String> getUserById(String userId) {
       
        
         Optional<User> opUser = userRepository.findById(Integer.parseInt(userId));
         Map<String , String> data = new HashMap<>();
         if(opUser.isPresent()){
            data.put("User", opUser.get().toString());
            return this.handleResponse(
                data,
                true,
                false,
                "User002",
                HttpStatus.OK
                );
         }else{
            data.put("Error", "User Not found");
            return this.handleResponse(
                data,
                true,
                true,
                "User003",
                HttpStatus.BAD_REQUEST
                );
         }
    }
    

    public ResponseEntity<String> handleResponse(Map<String,String> data , boolean hData,boolean error , String statusInternal , HttpStatus status ){
         UnifiedResponse unifiedResponse = new UnifiedResponse();
        unifiedResponse.setData(data);
        unifiedResponse.setHaveError(error);
        unifiedResponse.setHaveData(hData);
        unifiedResponse.setStatusInternalCode(statusInternal);
        return new ResponseEntity<>(unifiedResponse.toString(), status);
    }

    public ResponseEntity<String> updateUserProfile(String userId, UserUpdateRequest request) {
        Optional<User> opUser = userRepository.findById(Integer.parseInt(userId));
        Map<String , String> data = new HashMap<>();
        if(opUser.isPresent()){
                User profile = opUser.get();
            if (request.getFirstName() != null) {
                profile.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                profile.setLastName(request.getLastName());
            }
         
            // update rest of info logic to do 
            
            profile.setUpdatedAt(Instant.now());
            userRepository.save(profile);


           data.put("User", opUser.get().toString());
           return this.handleResponse(
               data,
               true,
               false,
               "User002",
               HttpStatus.OK
               );
        }else{
           data.put("Error", "User Not found");
           return this.handleResponse(
               data,
               true,
               true,
               "User003",
               HttpStatus.BAD_REQUEST
               );
        }
          
       
      
    }
    

    public ResponseEntity<String> updateUserRole(String userId, Role role) {
        Optional<User> opUser = userRepository.findById(Integer.parseInt(userId));
        Map<String , String> data = new HashMap<>();
        if(opUser.isPresent()){
                User profile = opUser.get();
                profile.setRole(role);
                profile.setUpdatedAt(Instant.now());
                 userRepository.save(profile);


           data.put("User", opUser.get().toString());
           return this.handleResponse(
               data,
               true,
               false,
               "User002",
               HttpStatus.OK
               );
        }else{
           data.put("Error", "User Not found");
           return this.handleResponse(
               data,
               true,
               true,
               "User003",
               HttpStatus.BAD_REQUEST
               );
        }
          
     
     
    }
    
   
}
