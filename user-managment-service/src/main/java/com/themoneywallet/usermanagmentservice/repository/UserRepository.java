package com.themoneywallet.usermanagmentservice.repository;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;

import com.themoneywallet.usermanagmentservice.entity.User;

public interface UserRepository extends CrudRepository<User , Integer> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
}
