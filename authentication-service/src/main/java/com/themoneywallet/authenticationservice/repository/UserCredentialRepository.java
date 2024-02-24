package com.themoneywallet.authenticationservice.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.themoneywallet.authenticationservice.entity.UserCredential;

public interface UserCredentialRepository extends CrudRepository<UserCredential , Integer> {
    Optional<UserCredential> findByEmail(String email);
}
