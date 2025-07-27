package com.themoneywallet.usermanagmentservice.service;

import com.themoneywallet.sharedUtilities.dto.event.Event;
import com.themoneywallet.sharedUtilities.dto.event.UserCreationEvent;
import com.themoneywallet.sharedUtilities.dto.response.UnifiedResponse;
import com.themoneywallet.sharedUtilities.enums.EventType;
import com.themoneywallet.sharedUtilities.enums.FixedInternalValues;
import com.themoneywallet.sharedUtilities.enums.ResponseKey;
import com.themoneywallet.sharedUtilities.enums.UserRole;
import com.themoneywallet.sharedUtilities.utilities.EventHandler;
import com.themoneywallet.sharedUtilities.utilities.JwtValidator;
import com.themoneywallet.sharedUtilities.utilities.SerializationDeHalper;
import com.themoneywallet.sharedUtilities.utilities.UnifidResponseHandler;
import com.themoneywallet.usermanagmentservice.dto.request.UserUpdateRequest;
import com.themoneywallet.usermanagmentservice.dto.response.UserPublicProfile;
import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtValidator jwtService;
    private final EventHandler eventHandler;
    private final UnifidResponseHandler unifidHandler;
    private final SerializationDeHalper serializationDeHelper;
    private final EventProducer eventProducer;

    public ResponseEntity<UnifiedResponse> deleteUser(String token) {
        String email = this.jwtService.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if (usr.isPresent()) {
            this.userRepository.delete(usr.get());
            this.publishDeleteEvent(usr.get());
            return this.unifidHandler.generateSuccessResponseNoBody("data", HttpStatus.OK);
        }
        return this.unifidHandler.generateFailedResponse("error", "Something goes wrong while deleting.", "AUVD1003", "String" , HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<UnifiedResponse> getProfile(String token) {
        String email = this.jwtService.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if (usr.isPresent()) {
            return this.unifidHandler.generateSuccessResponse("data", usr.get(),HttpStatus.OK);
        }
        return this.unifidHandler.generateFailedResponse("error", "User not found.", "URUD0001", "String" , HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<UnifiedResponse> updateProfile(UserUpdateRequest newUserData, String token) {
        String email = this.jwtService.extractUserName(token);
        Optional<User> usr = this.userRepository.findByEmail(email);
        if (usr.isPresent()) {
            User user = usr.get();
            BeanUtils.copyProperties(newUserData, user);
            user.setUpdatedAt(LocalDateTime.now());
            this.userRepository.save(user);
            this.publishUpdateEvent(user);
            return this.unifidHandler.generateSuccessResponse("data", user,HttpStatus.OK);
        }
        return this.unifidHandler.generateFailedResponse("error", "User not found.", "URUD0002", "String" , HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<UnifiedResponse> getAnotherProfile(String userName) {
      Optional<User> usr = this.userRepository.findByUserName(userName);
        if (usr.isPresent()) {
            User user = usr.get();
            UserPublicProfile userPublicProfile = new UserPublicProfile();
            BeanUtils.copyProperties(user, userPublicProfile);
            return this.unifidHandler.generateSuccessResponse("data", userPublicProfile,HttpStatus.OK);
        }
        return this.unifidHandler.generateFailedResponse("error", "User not found.", "URUD0003", "String" , HttpStatus.BAD_REQUEST);
    }


    public boolean publishDeleteEvent(User user){
        String data = this.serializationDeHelper.serailization(user);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.AUTH_USER_LOGIN_SUCCESSED,user.getUserId(), "data", data);
            this.eventProducer.publishProfileDeleted(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public boolean publishUpdateEvent(User user){
        String data = this.serializationDeHelper.serailization(user);
        if (data.equals(FixedInternalValues.ERROR_HAS_OCCURED.toString())) {
            return false;
        }
        try{
            Event event = this.eventHandler.makeEvent(EventType.USER_PROFILE_UPDATED,user.getUserId(), "data", data);
            this.eventProducer.publishProfileUpdated(event);
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public void createProfile(Event event) {
        UserCreationEvent user = new UserCreationEvent();
        BeanUtils.copyProperties(event.getAdditionalData().get(ResponseKey.DATA.toString()).get("data"), user);
        this.userRepository.save(User.builder().userId(user.getUserId()).userName(user.getUserName()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).userRole(UserRole.ROLE_USER).locked(user.isLocked()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
    }
 

}
