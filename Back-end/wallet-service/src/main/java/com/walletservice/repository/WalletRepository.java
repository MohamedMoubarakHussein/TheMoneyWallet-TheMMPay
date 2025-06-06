package com.walletservice.repository;

import com.walletservice.entity.Wallet;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends CrudRepository<Wallet, String> {
    List<Wallet> findAllByUserId(String id);

    @Query(
        "SELECT w FROM Wallet w WHERE w.id = :walletId AND w.userId = :userId"
    )
    Wallet findByUserIdAndId(
        @Param("userId") String userId,
        @Param("walletId") String walletId
    );
}
