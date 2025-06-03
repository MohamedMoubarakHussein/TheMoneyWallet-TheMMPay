package com.themoneywallet.authenticationservice.repository;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserCredentialRepository
    extends CrudRepository<UserCredential, String> {
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByUserName(String userName);
    Optional<UserCredential> findByToken(String token);
}
