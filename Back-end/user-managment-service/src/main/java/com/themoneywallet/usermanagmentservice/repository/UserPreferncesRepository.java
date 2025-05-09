package com.themoneywallet.usermanagmentservice.repository;



import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.themoneywallet.usermanagmentservice.entity.User;

@Repository
public interface UserPreferncesRepository extends CrudRepository<User , Integer> {
 
}
