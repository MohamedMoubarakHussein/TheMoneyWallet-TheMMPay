package com.walletservice.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.walletservice.entity.Wallet;

public interface WalletRepository  extends CrudRepository<Wallet, Long>{
     List<Wallet> findAllByUserId(Long id);

    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId AND w.userId = :userId")
    Wallet findByUserIdAndId(@Param("userId") Long userId , @Param("walletId") Long walletId);
}
