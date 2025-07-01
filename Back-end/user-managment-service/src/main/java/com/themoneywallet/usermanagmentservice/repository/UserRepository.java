package com.themoneywallet.usermanagmentservice.repository;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.themoneywallet.usermanagmentservice.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User , String> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    Iterable<User> findAll();
    Optional<User> findById(String id);
    boolean existsByEmail(String email);
}
