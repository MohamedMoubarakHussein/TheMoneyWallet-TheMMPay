package com.themoneywallet.authenticationservice.repository;

import com.themoneywallet.authenticationservice.entity.UserCredential;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialRepository extends CrudRepository<UserCredential, Integer> {
    
    Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByUserId(UUID id);
    Optional<UserCredential> findByUserName(String userName);
    Optional<UserCredential> findByToken(String token);
  
    @Query("SELECT u FROM UserCredential u WHERE u.email = :email OR u.userName = :userName")
    Optional<UserCredential> findByEmailOrUserName(@Param("email") String email, @Param("userName") String userName);

}
