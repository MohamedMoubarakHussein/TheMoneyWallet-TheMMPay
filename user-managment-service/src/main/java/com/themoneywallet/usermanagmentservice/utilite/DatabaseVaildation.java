package com.themoneywallet.usermanagmentservice.utilite;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.themoneywallet.usermanagmentservice.entity.User;
import com.themoneywallet.usermanagmentservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseVaildation {
    private final UserRepository userRepository;

    public boolean isUserNameExist(String userName){
       Optional<User> user = this.userRepository.findByUserName(userName);
        return user.isPresent();
    }

    public boolean isEmailNameExist(String email){
        Optional<User> user = this.userRepository.findByEmail(email);
        return user.isPresent();
    }


}
